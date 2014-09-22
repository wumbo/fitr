package data.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.lang.Math;

import data.persistant.Persistent;

/**
 * This class provides an abstract version of activity events provided by a
 * fitness tracking device. Each event consists of a collection of dataPoints
 * and attributes that relate to the activity event.
 * 
 * @author Fitr.Team
 */
public class Event implements Serializable {

	private static final long serialVersionUID = 2803079853022188247L;

	private String eventName;
	private Calendar startTime = new GregorianCalendar();
	private Calendar finishTime = new GregorianCalendar();
	private double distance; // in meters
	private double maxSpeed;
	private double averageSpeed;
	private int averageHeartRate;
	private int maxHeartRate;
	private double caloriesBurned;
	private ArrayList<DataPoint> points = new ArrayList<DataPoint>();
	private boolean hasBradycardia = false;

	/**
	 * Constructor
	 * 
	 * @param eventName
	 * @param points
	 *            All data points for the event
	 */

	public Event(String eventName, ArrayList<DataPoint> points) {
		this.eventName = eventName;
		this.points = points;
		calculate();
		calculateStress();
		calculateWarnings();
	}

	/**
	 * Calculates max and average speed, using the data points
	 */
	private void calculate() {
		int numPoints = points.size();
		int totalHR = 0;

		for (DataPoint point : points) {
			distance += point.getDistance();
			totalHR += point.getHeartRate();
			caloriesBurned += point.getCalories();
			if (maxSpeed < point.getSpeed()) {
				maxSpeed = point.getSpeed();
			}

			if (maxHeartRate < point.getHeartRate()) {
				maxHeartRate = point.getHeartRate();
			}
		}

		startTime = points.get(0).getDate();
		finishTime = points.get(points.size() - 1).getDate();
		averageHeartRate = totalHR / numPoints;
		averageSpeed = distance / getDuration();
	}

	private void calculateStress() {
		double sf = calculateStressFactor();
		double stress;

		for (DataPoint p : points) {
			if (p.getSpeed() != 0) {
				stress = sf * (p.getHeartRate() / p.getSpeed());
				stress = 0.5 + (1 / Math.PI)
						* Math.atan((Math.PI / 100) * (stress - 1));
				p.setStressLevel(stress);
			}
		}
	}

	/**
	 * Calculate if the user has bradycardia. Bradycardia is if the users
	 * heartrate is < 60 bpm If 5% of the points are less than 60bpm the user
	 * has bradycardia
	 */
	private void calculateWarnings() {
		if (Persistent.getCurrentUser() != null) {
			double warnings = 0;
			double count = 0;
			for (DataPoint p : points) {
				count++;
				if (p.getHeartRate() < 60) {
					warnings++;
				}
			}
			if (((warnings / count) * 100) >= 5.0) {
				hasBradycardia = true;
			}
		}
	}

	private double calculateStressFactor() {
		double totalSpeed = 0.0;
		int totalHeartRate = 0;
		double avgSpeed, avgHeartRate;

		for (DataPoint p : points) {
			totalSpeed += p.getSpeed();
			totalHeartRate += p.getHeartRate();
		}

		avgSpeed = totalSpeed / (points.size() - 1);
		avgHeartRate = (float) totalHeartRate / points.size();

		if (avgHeartRate == 0) {
			return 0.0; // You are DEAD and therefore are not physically
						// stressed
		}
		return avgSpeed / avgHeartRate;
	}

	/**
	 * Prints the name of the activity event.
	 */
	public void printEventName() {
		System.out.println(eventName);
	}

	/**
	 * Returns the average speed of the activity event.
	 * 
	 * @return The average speed in meters per second
	 */
	public double getAverageSpeed() {
		return averageSpeed;
	}

	/**
	 * Returns the max speed of the activity event.
	 * 
	 * @return The max speed in meters per second.
	 */
	public double getMaxSpeed() {
		return maxSpeed;
	}

	/**
	 * Returns the average heart rate of the activity event.
	 * 
	 * @return The average heart rate in beats per minute.
	 */
	public int getAverageHeartRate() {
		return averageHeartRate;
	}

	/**
	 * Returns the max heart rate of the activity event.
	 * 
	 * @return The max heart rate in beats per minute.
	 */
	public int getMaxHeartRate() {
		return maxHeartRate;
	}

	/**
	 * Returns the total distance traveled in the activity event.
	 * 
	 * @return The total distance in meters.
	 */
	public double getDistance() {
		return distance;
	}

	/**
	 * Returns the name of the activity event.
	 * 
	 * @return A String of containing the activity event name.
	 */
	public String getEventName() {
		return eventName;
	}

	/**
	 * Returns the start time of the activity event.
	 * 
	 * @return The start time of the activity event as a calendar object.
	 */
	public Calendar getStartTime() {
		return startTime;
	}

	/**
	 * Returns the finish time of the activity event.
	 * 
	 * @return The finish time of the activity event as a calendar object
	 */
	public Calendar getFinishTime() {
		return finishTime;
	}

	public ArrayList<DataPoint> getPoints() {
		return points;
	}

	/**
	 * Returns the duration of the activity seconds.
	 * 
	 * @return The duration for the activity event in seconds.
	 */
	public long getDuration() {
		return (finishTime.getTimeInMillis() - startTime.getTimeInMillis()) / 1000;
	}

	/**
	 * Returns a string formated to HH:MM:SS for the duration of the event to be
	 * displayed in the event summary.
	 * 
	 * @return A string of the activity events duration.
	 */
	public String getDurationString() {
		StringBuilder durationString = new StringBuilder();
		long duration = getDuration();
		long days = duration / (3600 * 24);
		duration -= days * 3600 * 24;
		long hours = duration / 3600;
		duration -= hours * 3600;
		long minutes = duration / 60;
		duration -= minutes * 60;
		long seconds = duration;

		if (days > 0) {
			durationString.append(String.format("%d day%s %d hour%s", days,
					days == 1 ? "" : "s", hours, hours == 1 ? "" : "s"));
		} else if (hours > 0) {
			durationString.append(String.format("%d hour%s %d minute%s", hours,
					hours == 1 ? "" : "s", minutes, minutes == 1 ? "" : "s"));
		} else if (minutes > 0) {
			durationString.append(String.format("%d minute%s %d second%s",
					minutes, minutes == 1 ? "" : "s", seconds,
					seconds == 1 ? "" : "s"));
		} else {
			durationString.append(String.format("%d second%s", seconds,
					seconds == 1 ? "" : "s"));
		}

		return durationString.toString();
	}

	/**
	 * Returns a nicely formatted distance string for use in the GUI.
	 * 
	 * @return The formatted distance string.
	 */
	public String getDistanceString() {
		String distanceString;
		if (getDistance() >= 1000.0) {
			distanceString = String.format("%.2f km", getDistance() / 1000);
		} else {
			distanceString = String.format("%d meter%s", (int) getDistance(),
					getDistance() == 1.0 ? "" : "s");
		}

		return distanceString;
	}

	public String getTimeString() {
		SimpleDateFormat tf = new SimpleDateFormat("EEEE, MMMM d, h:mm a");

		return String.format("%s", tf.format(startTime.getTime()));
	}

	/**
	 * Returns the data points of the activity event.
	 * 
	 * @return The dataPoints inside the activity event.
	 */
	public ArrayList<DataPoint> getDataPoints() {
		return this.points;
	}

	/**
	 * returns calories burned for the event
	 * 
	 * @return calories burned
	 */
	public double getCaloriesBurned() {
		return caloriesBurned;
	}

	/**
	 * A method which takes a dataPoint and returns a string of the location in
	 * the form "lat, long"
	 * 
	 * @param point
	 *            A single dataPoint
	 * @return A string of the location of the dataPoint in the form "lat,long"
	 */
	public String getPointString(DataPoint point) {
		String latStr;
		String lonStr;
		StringBuilder pointString = new StringBuilder();

		latStr = String.format("%s", point.getLatitude());
		try {
			latStr = latStr.substring(0, 12);
		} catch (StringIndexOutOfBoundsException e) {
		}
		lonStr = String.format("%s", point.getLongitude());
		try {
			lonStr = lonStr.substring(0, 12);
		} catch (StringIndexOutOfBoundsException e) {
		}
		pointString.append(latStr);
		pointString.append(",");
		pointString.append(lonStr);
		return pointString.toString();
	}

	/**
	 * This method creates a string of Point Locations which is the path taken
	 * for the event
	 * 
	 * @return A string of locations, in the form "lat, long", which is a path
	 *         for the event.
	 */
	public String getPathString() {
		int MAX_POINTS = 68; // Due to 2048 char limit for static maps.
		ArrayList<DataPoint> dataPoints = getDataPoints();
		int dataSize = dataPoints.size();
		StringBuilder pathBuilder = new StringBuilder();

		pathBuilder.append(getPointString(dataPoints.get(0)));
		pathBuilder.append("|");
		for (int i = 1; i < dataSize - 1; i += ((dataSize - 1) / MAX_POINTS) + 1) {
			pathBuilder.append(getPointString(dataPoints.get(i)));
			pathBuilder.append("|");
		}
		pathBuilder.append(getPointString(dataPoints.get(dataSize - 1)));

		return pathBuilder.toString();
	}

	public void getLastday() {

	}

	public boolean hasBradycardia() {
		return hasBradycardia;
	}

	public String avgHRString() {
		return String.format("%d bpm", getAverageHeartRate());
	}

	public String maxHRString() {
		return String.format("%d bpm", getMaxHeartRate());
	}

	public String avgSpeedString() {
		return String.format("%.2f km / h", getAverageSpeed() * 3.6);
	}

	public String maxSpeedString() {
		return String.format("%.2f km / h", getMaxSpeed() * 3.6);
	}

	public String getCaloriesString() {
		return String.format("%.0f", getCaloriesBurned());
	}

	@Override
	public boolean equals(Object other) {
		boolean equal = false;
		if (other != null && other.getClass() == Event.class) {
			Event e = (Event) other;
			if (e.getStartTime().equals(startTime)
					&& e.getEventName().equals(eventName)) {
				equal = true;
			}
		}
		return equal;

	}
}
