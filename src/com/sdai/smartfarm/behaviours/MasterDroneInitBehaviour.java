package com.sdai.smartfarm.behaviours;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.sdai.smartfarm.Main;
import com.sdai.smartfarm.agents.AgentType;
import com.sdai.smartfarm.agents.DroneAgent;
import com.sdai.smartfarm.settings.SimulationSettings;

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

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;


import tutorial.clustering.SameSizeKMeans;

public class MasterDroneInitBehaviour extends OneShotBehaviour {

    protected static SimulationSettings simulationSettings = SimulationSettings.defaultSimulationSettings();

    private static final Logger LOGGER = Logger.getLogger(MasterDroneInitBehaviour.class.getName());

    static {
        LOGGER.addHandler(Main.HANDLER);
    }

    @Override
    public void action() {

        LOGGER.info("The Drone Master is deciding the Tiles assignment");

        DroneAgent agent = (DroneAgent) getAgent(); // Only a DroneAgent can have the MasterDroneInitBehaviour

        AID[] receivers = agent.getKnown(AgentType.DRONE);

        List<List<int[]>> clusters = clusterize(agent.getFields(), receivers.length);

        try {
            for(int i = 0; i < receivers.length; i++) {
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.addReceiver(receivers[i]);
                msg.setLanguage("English");
                msg.setOntology("Region-Assignment");
                msg.setContentObject((Serializable) clusters.get(i));
                agent.send(msg);
            }
            LOGGER.info("The Drone Master has sent the Tiles assignment");
            LOGGER.warning("The Drone Master has sent the Tiles assignment");

        } catch(IOException e) {
            LOGGER.severe("cluster was not serializable...");
            throw new IllegalStateException();
        }
        
    }

    protected List<List<int[]>> clusterize(List<List<int[]>> fields, int numClusters) {

        List<IntegerVector> farmPoints = new ArrayList<>();

        for(List<int[]> field : fields) {
            farmPoints.addAll(field.stream().map(IntegerVector::new).toList());
        }

        DatabaseConnection fakeConnection2 = new IntegerVectorDatabaseConnection(farmPoints);
            
        Database db = new StaticArrayDatabase(fakeConnection2, null);
        db.initialize();
        
        Relation<NumberVector> relation = db.getRelation(TypeUtil.NUMBER_VECTOR_FIELD);

        if(relation == null) throw new IllegalStateException("This should not be happening. I honestly don't even know how to recover from this");
        
        RandomFactory randomFactory = RandomFactory.DEFAULT;
        KMeansInitialization initialization = new KMeansPlusPlus<NumberVector>(randomFactory);

        SameSizeKMeans<NumberVector> kmeans = new SameSizeKMeans<>(EuclideanDistance.STATIC, numClusters, 100, initialization);

        Clustering<MeanModel> result = kmeans.run(relation);

        List<List<int[]>> clusters = new ArrayList<>();

        int clusterId = 0;

        for(Cluster<MeanModel> cluster: result.getAllClusters()) {
            DBIDs ids = cluster.getIDs();

            clusters.add(new ArrayList<>());

            List<int[]> clusterAsList = clusters.get(clusterId);

            ids.forEach(id -> {
                NumberVector vec = relation.get(id); 
                int x = vec.intValue(0);
                int y = vec.intValue(1);
                clusterAsList.add(new int[] {x, y});
                 
            });

            clusterId++;
          
        }

        return clusters;

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
        bundle.appendColumn(TypeUtil.NUMBER_VECTOR_FIELD, data);
        
        return bundle;
    }

}
