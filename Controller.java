package application;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

class Resume{
    protected String education;
    protected String past_experience;
    protected String skills;

    public Resume()
    {

    }
    public Resume(String education, String past_experience, String skills){
        this.education = education;
        this.past_experience = past_experience;
        this.skills = skills;
    }
    public void display_resume(){
        System.out.println("Education: " + education);
        System.out.println("Experience: " + past_experience);
        System.out.println("Skills: " + skills);
    }
}

class Company
{
    public String C_name;
    protected String address;
    protected String email;
    public Company(){}
    public Company(String C_name, String address, String email){
        this.C_name = C_name;
        this.address = address;
        this.email = email;
    }
}

abstract class profile{
    protected String name;
    protected String username;
    protected String email;
    protected String password;
}


class Endorsement {
	   private String jobHunterUsername;
	   private String employerUsername;
	   private String description;
	   private String date;
	   public Endorsement(String jobHunterUsername, String employerUsername, String description, String date) {
	       this.jobHunterUsername = jobHunterUsername;
	       this.employerUsername = employerUsername;
	       this.description = description;
	       this.date = date;
	   }
	   public String getJobHunterUsername() {
	       return jobHunterUsername;
	   }
	   public String getEmployerUsername() {
	       return employerUsername;
	   }
	   public String getDescription() {
	       return description;
	   }
	   public String getDate() {
	       return date;
	   }
	}



class job_hunter extends profile{
    protected Resume resume;
    public Company company;
    public job_hunter(){}
    public job_hunter(String name,String username,String email,String password){
        this.name=name;
        this.username=username;
        this.email=email;
        this.password=password;
    }
    public void add_company(Company c)
    {
        company=c;
    }
    public void attach_resume(Resume r){
        resume=r;
    }
}
class employer extends profile{
    Company company;
    public employer(String name,String username,String email,String password){
        this.name=name;
        this.username=username;
        this.email=email;
        this.password=password;

    }
}

class Recruiter extends profile{
    public Recruiter(String name,String username,String email,String password){
    this.name=name;
    this.username=username;
    this.email=email;
    this.password=password;
    }
}

class application
{
   String companyName;
   String vacancyTitle;
   String requirements;
   String date;
   String username;
   String status = "Pending";
   public void update_status(String newStatus)
   {
   	Controller.db.update_application(newStatus,vacancyTitle,username);
   }
}


class job_vacancy
{
   public Company company;
   public  String details;
   public   String requirements;
   public   String location;
   public String date_posted;
   public String deadline;
    public ArrayList<application> applications;
    public  Recruiter recruiter;
}


public class Controller {

	public static job_hunter Current_JH;
	public static Recruiter Current_R;
	public static employer Current_E;


	//login
	@FXML
    private TextField usernameField; // For username input
    @FXML
    private PasswordField passwordField; // For password input

    static DBHandler db = DBHandler.getInstance();
    static String curr_user;
    static String curr_type;
    @FXML
    public void login() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String name;
        String email;

        if (db.isProfile(username)) {
            Map<String, String> user = db.verify(username, password);
            if (user != null) {
                curr_user = user.get("username");
                curr_type = user.get("type");
                name = user.get("name");
                email = user.get("email");

                showAlert(AlertType.INFORMATION,
                    "Login Successful",
                    "Welcome, " + curr_user + " [" + curr_type + "]"
                );

                UserSession.currentUsername = curr_user;
                UserSession.currentRole = curr_type;

                if ("JobHunter".equals(UserSession.currentRole)) {
                    Current_JH = new job_hunter(name, curr_user, email, password);
                    goToAccount();
                } else if ("Employer".equals(UserSession.currentRole)) {
                    Current_E = new employer(name, curr_user, email, password);
                    goToEmployerDashboard();
                } else if ("Recruiter".equals(UserSession.currentRole)) {
                    Current_R = new Recruiter(name, curr_user, email, password);
                    goToRecruiterAccount();
                }
            } else {
                showAlert(AlertType.ERROR,
                    "Login Error",
                    "Invalid username or password."
                );
            }
        } else {
            showAlert(AlertType.ERROR,
                "Profile Not Found",
                "No profile found with the provided username."
            );
        }
    }

    private void showAlert(AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    //registration
    @FXML
    private TextField jhnameField, jhusernameField, jhemailField , companyField;

    @FXML
    private PasswordField jhpasswordField;

    @FXML
    public void register_job_hunter() {
        String username = jhusernameField.getText();
        String password = jhpasswordField.getText();
        String email = jhemailField.getText();
        String name = jhnameField.getText();

        if (db.isProfile(username)) {
            showAlert(AlertType.WARNING,
                "Registration Error",
                "Profile already exists. Please login instead."
            );
            return;
        }

        Current_JH = new job_hunter(name, username, email, password);
        db.addProfile(username, name, email, password, "JobHunter");
        db.add_jobhunter(name, username, password, email);
        curr_user = username;
        curr_type = "JobHunter";

        showAlert(AlertType.INFORMATION,
            "Registration Successful",
            "Welcome, " + curr_user
        );

        UserSession.currentRole = curr_type;
        UserSession.currentUsername = curr_user;
        goToResumeBuilder();
    }

    @FXML
    public void goToEmployerDashboard()
    {
    	try {
    		 FXMLLoader loader = new FXMLLoader(getClass().getResource("userInterface/employerDashboard.fxml"));
             Scene accountScene = new Scene(loader.load());
             Stage stage = (Stage) usernameField.getScene().getWindow();
             stage.setScene(accountScene);
    	} catch (IOException e ) {
    		e.printStackTrace();
    	}
    }

    @FXML
    public void goToRecruiterAccount()
    {
    	System.out.println("Loading Recruiter Account");
    	try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("userInterface/recruiteraccount.fxml"));
            Scene RecruiterAccountScene = new Scene(loader.load());
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(RecruiterAccountScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void goToResumeBuilder() {
    	  try {
              FXMLLoader loader = new FXMLLoader(getClass().getResource("userInterface/ResumeBuilder.fxml"));
              Scene ResumeScene = new Scene(loader.load());
              Stage stage = (Stage) jhusernameField.getScene().getWindow();
              stage.setScene(ResumeScene);
          } catch (IOException e) {
              e.printStackTrace();
          }
    }

    @FXML
    public void goToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("userInterface/login.fxml"));
            Scene loginScene = new Scene(loader.load());
            Stage stage = (Stage) jhusernameField.getScene().getWindow();
            stage.setScene(loginScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void goToAccount() {
    	try {
    		System.out.println("Controller.goToAccount()");
    		 FXMLLoader loader = new FXMLLoader(getClass().getResource("userInterface/account1.fxml"));
             Scene accountScene = new Scene(loader.load());
             Stage stage = (Stage) usernameField.getScene().getWindow();
             stage.setScene(accountScene);

    	} catch (IOException e ) {
    		e.printStackTrace();
    	}
    }
    @FXML
    public void goToRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("userInterface/registerJH.fxml"));
            Scene registerScene = new Scene(loader.load());
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(registerScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void register_employer() {
        String username = jhusernameField.getText().trim();
        String password = jhpasswordField.getText().trim();
        String email = jhemailField.getText().trim();
        String name = jhnameField.getText().trim();
        String company_name = companyField.getText().trim();

        if (db.isProfile(username)) {
            showAlert(AlertType.WARNING,
                "Registration Error",
                "Profile already exists. Please log in."
            );
            return;
        }

        if (!db.isCompany(company_name)) {
            showAlert(AlertType.ERROR,
                "Company Error",
                "Company does not exist. Please register the company first."
            );
            return;
        }

        db.addProfile(username, name, email, password, "Employer");
        db.add_employer(name, username, password, email, company_name);
        curr_user = username;
        curr_type = "Employer";

        showAlert(AlertType.INFORMATION,
            "Registration Successful",
            "Welcome, " + name + "!"
        );

        goToLogin();
    }

    public void register_recruiter() {
        String username = jhusernameField.getText().trim();
        String password = jhpasswordField.getText().trim();
        String email = jhemailField.getText().trim();
        String name = jhnameField.getText().trim();

        if (db.isProfile(username)) {
            showAlert(AlertType.WARNING,
                "Registration Error",
                "Profile already exists. Please log in."
            );
            return;
        }

        db.addProfile(username, name, email, password, "Recruiter");
        db.add_recruiter(name, username, password, email);
        curr_user = username;
        curr_type = "Recruiter";

        showAlert(AlertType.INFORMATION,
            "Registration Successful",
            "Welcome, " + name + "!"
        );

        goToLogin();
    }

}