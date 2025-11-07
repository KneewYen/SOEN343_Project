package org.ridewithus.domain.pricing.strategy;

import org.ridewithus.domain.entity.Trip;

public class DistanceStrategy implements PricingStrategy{
    
    private final double EARTH_RADIUS = 6371;
    private double distanceRate = 0.02; //per meter

    @Override
    public double calculatePrice(Trip trip){
        double start_lat = trip.getStartStation().getLatitude();
        double start_long = trip.getStartStation().getLongitude();

        double end_lat = trip.getEndStation().getLatitude();
        double end_long = trip.getEndStation().getLongitude();

        System.out.println("Startstation: " + start_lat + "," + start_long);
        System.out.println("Endstation: " + end_lat + "," + end_long);

        double difference_lat = Math.toRadians(end_lat - start_lat);
        double difference_long = Math.toRadians(end_long - start_long);

        double startLat = Math.toRadians(start_lat);
        double endLat = Math.toRadians(end_lat);

        double a = haversine(difference_lat) + Math.cos(startLat) * Math.cos(endLat) * haversine(difference_long);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distanceMeter = EARTH_RADIUS * c * 1000;

        System.out.println("distance"+distanceMeter);
        System.out.println("price5"+distanceMeter*distanceRate);
        return distanceMeter * distanceRate;
    }

    double haversine(double val) {
        return Math.pow(Math.sin(val / 2), 2);
    }

    public String getName(){
        return "Base plan";
    }

    public String getDescription(){
        return "Per-distance plan (0.02$/meter)";
    }
}
