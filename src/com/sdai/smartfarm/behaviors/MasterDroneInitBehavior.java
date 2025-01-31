package com.sdai.smartfarm.behaviors;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import elki.data.Cluster;
import elki.data.Clustering;
import elki.data.IntegerVector;
import elki.data.NumberVector;
import elki.data.model.MeanModel;
import elki.data.type.SimpleTypeInformation;
import elki.data.type.TypeInformation;
import elki.data.type.TypeUtil;
import elki.datasource.ArrayAdapterDatabaseConnection;
import elki.datasource.BundleDatabaseConnection;
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

public class MasterDroneInitBehavior extends InitBehaviour {

    @Override
    public void action() {

        super.action();

        clusterize();

    }

    protected void clusterize() {
        /*List<IntegerVector> farmPoints = new ArrayList<>();
        for(List<IntegerVector> field : fields) {
            // this is also inefficient, I should check whether it can be done with just views if I have time
            farmPoints.addAll(field);
        }*/

        double[][] data = new double[100][2];

        Random rng = new Random();

        for(int i = 0; i < 100; i++) {
            data[i][0] = rng.nextDouble() * 1000;
            data[i][1] = rng.nextDouble() * 1000;
        }


        ArrayAdapterDatabaseConnection fakeConnection = new ArrayAdapterDatabaseConnection(data);

        //DatabaseConnection fakeConnection2 = new IntegerVectorDatabaseConnection(farmPoints);
            
        Database db = new StaticArrayDatabase(fakeConnection, null);
        db.initialize();
        
        Relation<NumberVector> relation = db.getRelation(TypeUtil.NUMBER_VECTOR_FIELD);

        if(relation == null) throw new IllegalStateException("This should not be happening. I honestly don't even know how to recover from this");
        
        RandomFactory randomFactory = RandomFactory.DEFAULT;
        KMeansInitialization initialization = new KMeansPlusPlus<NumberVector>(randomFactory);

        int clusterNumber = simulationSettings.dronesNumber();
        SameSizeKMeans<NumberVector> kmeans = new SameSizeKMeans<>(EuclideanDistance.STATIC, clusterNumber, 100, initialization);

        Clustering<MeanModel> result = kmeans.run(relation);

        int clusterId = 0;

        for(Cluster<MeanModel> cluster: result.getAllClusters()) {
            DBIDs ids = cluster.getIDs();

            System.out.println("\n\nCLUSTER: " + clusterId);
            ids.forEach((id) -> {
                NumberVector vec = relation.get(id); 
                System.out.println("    has point: " + vec.doubleValue(0) + ", " + vec.doubleValue(1));  
            });

            clusterId++;
          
        }


    }

}

class IntegerVectorDatabaseConnection implements DatabaseConnection {
    private final List<IntegerVector> data;

    public IntegerVectorDatabaseConnection(List<IntegerVector> data) {
        this.data = data;
    }

    @Override
    public MultipleObjectsBundle loadData() {
        MultipleObjectsBundle bundle = new MultipleObjectsBundle();
        data.forEach(bundle::appendSimple);
        
        return bundle;
    }

}
