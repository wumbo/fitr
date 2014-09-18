package data.persistant;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import data.model.EventContainer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import user.User;

/**
 * static class for keeping track of preferences, file paths etc
 * 
 * @author SamSchofield
 *
 */
public class Persistent {

	private static Preferences prefs = Preferences.userRoot().node("/Fitr");
	private static ObservableList<User> users = FXCollections.observableList(new ArrayList<User>());
	private static ObservableList<String> userNames = FXCollections.observableList(new ArrayList<String>());
	private static User currentUser;

	/**
	 * sets the FilePath preference to a Fitr directory at location of filePath
	 * 
	 * @param filePath
	 */
	public static void setFilePath(String filePath) {
		// filePath will be obtained from a file chooser so will always be an existing
		prefs.put("FilePath", filePath + "/Fitr/");
		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}

		// only creates new directory if the selected one doesn't already exist
		if (!new File(getFilePath()).exists()) {
			System.out.println("Setting up directory" + filePath);
			setupDirectory();
		} else {
			// if the Fitr directory already exists then use it.
			Persistent.initialize();
		}
	}

	/**
	 * gets the file path from preferences
	 * 
	 * @return FilePath
	 */
	public static String getFilePath() {
		return prefs.get("FilePath", null);
	}

	/**
	 * sets up a new Fitr directory with a users sub directory at the file path
	 */
	public static void setupDirectory() {
		if (getFilePath() != null) {
			new File(getFilePath()).mkdirs();
		}
	}

	/**
	 * returns the file path to the current users profile
	 * 
	 * @return profileFilePath
	 */
	public static String getProfileFilePath(String userName) {
		return getFilePath() + userName + "/" + userName + ".fitr";
	}

	/**
	 * checks if a valid file path has been set
	 * 
	 * @return pathSet
	 */
	public static boolean filePathSet() {
		boolean pathSet = true;
		File filePath;
		System.out.println(getFilePath());
		if (getFilePath() == null) {
			filePath = null;
		} else {
			System.out.println(getFilePath() + "is not null");
			filePath = new File(getFilePath());
			System.out.println("File path exists = " + filePath.exists());
		}

		if (filePath == null || !filePath.exists()) {
			pathSet = false;
		}
		return pathSet;
	}

	/**
	 * creates a new directory in users with name user to store information in
	 * also adds a user.json and activity.json file to save data to
	 * 
	 * @param userName
	 */
	public static void newUser(User user) throws Exception {
		String userName = user.getName();

		if (!userNames.contains(userName)) {
			new File(getFilePath() + userName).mkdir();

			try {
				new File(getProfileFilePath(userName)).createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}

			users.add(user);
			userNames.add(user.getName());
			Saver.SaveUser(user);
			prefs.putInt("LastUserID", prefs.getInt("LastUserID", 0) + 1);
		} else {
			System.out.println("User has already beeen created. try different username");
			// throw new Exception("User already exists");
		}

	}

	public static int getLastUserID() {
		return prefs.getInt("LastUserID", 0);
	}

	/**
	 * Sets the current user
	 * 
	 * @param user
	 */
	public static void setUser(User user) {
		currentUser = user;
	}

	/**
	 * returns the current user
	 * 
	 * @return currenUser
	 */
	public static User getCurrentUser() {
		return currentUser;
	}

	/**
	 * returns an ArrayList of all the users found in the user directories
	 * 
	 * @return ArrayList<String>
	 */
	public static ObservableList<User> getUsers() {
		return users;
	}

	/**
	 * gets the user names of all the profiles
	 * 
	 * @return userNames
	 */
	public static ObservableList<String> getUserNames() {
		for (User user : users) {
			userNames.add(user.getName());
		}
		return userNames;
	}

	/**
	 * loads all the profiles from the users directory into the
	 * ObservableList<User> users
	 */
	public static void initialize() {
		if (getFilePath() != null && new File(getFilePath()).exists()) {
			File filePath = new File(getFilePath());

			// get files / directory in Fitr directory (gets users)
			File[] Files = filePath.listFiles();

			for (File file : Files) {
				String userName = file.getName();

				if (new File(file + "/" + userName + ".fitr").exists()) { 
					// check if the file is a user
					User newUser = Loader.loadUserProfile(new File(file + "/"+ userName + ".fitr"));
					users.add(newUser);
					userNames.add(newUser.getName());
				}
			}
		}
	}
	
	public static void clear() {
		try {
			prefs.clear();
		} catch (BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String args[]) throws Exception {

		/*
		 * setFilePath("/Users/SamSchofield/Desktop"); setupDirectory();
		 * initialize(); System.out.println("______________________"); User u =
		 * new User("a", null, null); User v = new User("b", null, null); //
		 * setUser(u); newUser(u); newUser(v);
		 * System.out.println(prefs.getInt("LastUserID", 0));
		 * 
		 * System.out.println(getFilePath()); System.out.println("Saved");
		 * System.out.println("Users are: ");
		 * 
		 * ArrayList<User> a = new ArrayList<User>(users); for (int i = 0; i <
		 * a.size(); i++) { System.out.println(a.get(i)); }
		 * 
		 * prefs.clear();
		 */
		// System.out.println("data cleared");
		
		EventContainer e = new EventContainer();
		User u = new User("Sam", null, null, 0, 0, e, 0);
		//User v = new User("Dan", null, null, 0, 0, e, 0);
		setUser(u);
		System.out.println(getCurrentUser());
		
		

	}

}
