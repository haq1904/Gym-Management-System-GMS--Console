
import com.gym.model.facilities.GymMachine;
import com.gym.model.users.User;
import com.gym.repository.*;
import com.gym.view.LoginMenu;
import java.util.List;


public class Main {



    public static void main(String[] args) {

        String userFilePath = "data/user.csv";
        String machineFilePath = "data/machine.csv";



        //Load danh sach user
        UserRepository userRepo = new UserRepository(userFilePath);
        List<User> userList = userRepo.loadData();
//        for(User user : userList){
//            System.out.println(user);
//        }

        //Load danh sach machine
        MachineRepository machineRepo = new MachineRepository(machineFilePath);
        List<GymMachine> machineList = machineRepo.loadData();
        for (GymMachine machine : machineList){
            System.out.println(machine);
        }

        //Lay GymContext
        GymContext gymContext = new GymContext(userRepo,userList,machineRepo,machineList);

        // Khoi tao menu login
        LoginMenu loginMenu = new LoginMenu();

        // Vong lap chinh chay chuong trinh
        while (true) {
            //Goi menu dang nhap , treo cho toi khi nguoi dung dang nhap và tra ve user
            User loggedInUser = loginMenu.displayLogin(userList);

            //Hien thi menu theo tung class duoc tra ve
            loggedInUser.displayMenu(gymContext);
        }

    }
}