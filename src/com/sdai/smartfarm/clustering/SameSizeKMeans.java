package com.sdai.smartfarm.clustering;

import elki.clustering.kmeans.AbstractKMeans;
import elki.clustering.kmeans.initialization.KMeansInitialization;
import elki.data.Clustering;
import elki.data.NumberVector;
import elki.data.model.MeanModel;
import elki.database.relation.Relation;
import elki.distance.NumberVectorDistance;
import elki.logging.Logging;

public class SameSizeKMeans<V extends NumberVector> extends AbstractKMeans<V, MeanModel> {

    /**
     * Class logger
     */
    private static final Logging LOG = Logging.getLogger(SameSizeKMeans.class);

    /**
     * Constructor.
     *
     * @param distance Distance function
     * @param k Number of neighbors
     * @param maxiter Maximum number of iterations
     * @param initializer
     */
    public SameSizeKMeans(
        NumberVectorDistance<? super V> distance,
        int k, int maxiter, KMeansInitialization initializer
    ) {
        super(distance, k, maxiter, initializer);
    }

    @Override
    protected Logging getLogger() {
        return LOG;
    }

    /**
   * Run k-means with cluster size constraints.
   *
   * @param relation relation to use
   * @return result
   */
  @Override
  public Clustering<MeanModel> run(Relation<V> relation) {
      return null;
  }
    
    
}
