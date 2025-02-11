package com.sdai.smartfarm.logic;

import java.util.ArrayList;
import java.util.List;

import com.sdai.smartfarm.Main;
import com.sdai.smartfarm.models.FarmField;
import com.sdai.smartfarm.models.Position;

import elki.data.Cluster;
import elki.data.Clustering;
import elki.data.IntegerVector;
import elki.data.NumberVector;
import elki.data.model.MeanModel;
import elki.data.type.TypeUtil;
import elki.datasource.DatabaseConnection;
import elki.datasource.bundle.MultipleObjectsBundle;
import elki.database.Database;
import elki.database.StaticArrayDatabase;
import elki.database.ids.DBIDs;
import elki.database.relation.Relation;
import elki.distance.minkowski.EuclideanDistance;
import elki.utilities.random.RandomFactory;
import elki.clustering.kmeans.initialization.KMeansPlusPlus;
import elki.clustering.kmeans.initialization.KMeansInitialization;

import tutorial.clustering.SameSizeKMeans;

public class BalancedKMeans {

    private BalancedKMeans() {}

    public static List<List<Position>> clusterize(List<FarmField> fields, int numClusters) {

        if (numClusters == 1) {
            // 1 is an edge case that breaks Elki

            List<Position> cluster = new ArrayList<>();
            for(FarmField field : fields) {
                cluster.addAll(field.positions());
            }
            return List.of(cluster);
        }

        List<IntegerVector> farmPoints = new ArrayList<>();

        for(FarmField field : fields) {
            farmPoints.addAll(field.positions().stream().map((Position p) -> new IntegerVector(new int[] {p.x(), p.y()})).toList());
        }

        DatabaseConnection fakeConnection = new IntegerVectorDatabaseConnection(farmPoints);
            
        Database db = new StaticArrayDatabase(fakeConnection, null);
        db.initialize();
        
        Relation<NumberVector> relation = db.getRelation(TypeUtil.NUMBER_VECTOR_FIELD);

        if(relation == null) throw new IllegalStateException("This should not be happening. I honestly don't even know how to recover from this");
        
        RandomFactory randomFactory = RandomFactory.DEFAULT;
        KMeansInitialization initialization = new KMeansPlusPlus<NumberVector>(randomFactory);

        SameSizeKMeans<NumberVector> kmeans = new SameSizeKMeans<>(EuclideanDistance.STATIC, numClusters, 100, initialization);

        Clustering<MeanModel> result = kmeans.run(relation);

        List<List<Position>> clusters = new ArrayList<>();

        int clusterId = 0;

        for(Cluster<MeanModel> cluster: result.getAllClusters()) {
            DBIDs ids = cluster.getIDs();

            clusters.add(new ArrayList<>());

            List<Position> clusterAsList = clusters.get(clusterId);

            ids.forEach(id -> {
                NumberVector vec = relation.get(id); 
                int x = vec.intValue(0);
                int y = vec.intValue(1);
                clusterAsList.add(new Position(x, y));
                 
            });

            clusterId++;
          
        }

        // restore logging because Elki breaks it
        Main.restoreLogging();

        return clusters;

    }

    private static class IntegerVectorDatabaseConnection implements DatabaseConnection {
        private final List<IntegerVector> data;

        public IntegerVectorDatabaseConnection(List<IntegerVector> data) {
            this.data = data;
        }

        @Override
        public MultipleObjectsBundle loadData() {
            MultipleObjectsBundle bundle = new MultipleObjectsBundle();
            bundle.appendColumn(TypeUtil.NUMBER_VECTOR_FIELD, data);
            
            return bundle;
        }

    }

}