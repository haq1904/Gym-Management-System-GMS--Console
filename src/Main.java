
import com.gym.model.*;
import com.gym.repository.*;
import com.gym.view.LoginMenu;
import java.util.List;


public class Main {
    public static void main(String[] args) {
        //Khoi tao doi tuong quan ly
        UserRepository userManager = new UserRepository();
        List<User> databaseUsers = userManager.loadUsers("data/user.csv");
        for(User user : databaseUsers){
            System.out.println(user);
        }

        // Khoi tao menu login
        LoginMenu loginMenu = new LoginMenu();

        // Vong lap chinh chay chuong trinh
        while (true) {
            //Goi menu dang nhap , treo cho toi khi nguoi dung dang nhap và tra ve user
            User loggedInUser = loginMenu.displayLogin(databaseUsers);

            //Hien thi menu theo tung class duoc tra ve
            loggedInUser.displayMenu();
        }

    }
}