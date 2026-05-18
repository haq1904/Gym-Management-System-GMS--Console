
import com.gym.model.facilities.GymMachine;
import com.gym.model.users.User;
import com.gym.repository.*;
import com.gym.view.LoginMenu;
import java.util.List;


public class Main {



    public static void main(String[] args) {
        //path file data
        String userFilePath = "data/user.csv";
        String machineFilePath = "data/machine.csv";

        //Khoi tao repo de cho viec load du lieu
        IRepository<User> userRepo = new UserRepository(userFilePath);
        IRepository<GymMachine> machineRepo = new MachineRepository(machineFilePath);

        //Lay GymContext
        GymContext gymContext = new GymContext(userRepo,machineRepo);

        // Khoi tao menu login
        LoginMenu loginMenu = new LoginMenu();

        // Vong lap chinh chay chuong trinh
        while (true) {
            //Goi menu dang nhap , treo cho toi khi nguoi dung dang nhap và tra ve user
            User loggedInUser = loginMenu.displayLogin(gymContext);
            loggedInUser.getMenu().displayMenu(gymContext,loggedInUser);
        }

    }
}