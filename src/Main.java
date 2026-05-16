import com.gym.model.Admin;
import com.gym.model.Member;
import com.gym.model.Trainer;
import com.gym.model.User;
import com.gym.view.LoginMenu;

import java.util.ArrayList;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        List<User> databaseUsers = new ArrayList<>();
        databaseUsers.add(new Admin("admin", "123", "Nguyễn Văn com.gym.model.Admin"));
        databaseUsers.add(new Trainer("pt01", "123", "HLV Trần Văn Cơ Bắp"));
        databaseUsers.add(new Member("gym01", "123", "Hội Viên Nguyễn Văn Tập"));

        // Khoi tao menu login
        LoginMenu loginMenu = new LoginMenu();

        // Vong lap chinh chay chuong trinh
        while (true) {
            //Goi menu dang nhap , treo cho toi khi nguoi dung dang nhap và tra ve user
            User loggedInUser = loginMenu.displayLogin(databaseUsers);

            // 3. ĐỈNH CAO ĐA HÌNH: Không cần dùng if-else để check role! [cite: 25]
            // Dù loggedInUser trả về là com.gym.model.Admin, com.gym.model.Trainer, hay com.gym.model.Member, Java tự động biết để gọi đúng menu tương ứng [cite: 24, 33]
            loggedInUser.displayMenu(); // [cite: 32, 33]

            // Sau khi người dùng chọn "Đăng xuất" (thoát khỏi hàm displayMenu),
            // vòng lặp while(true) sẽ tự động quay lại màn hình Login ban đầu.
        }

    }
}