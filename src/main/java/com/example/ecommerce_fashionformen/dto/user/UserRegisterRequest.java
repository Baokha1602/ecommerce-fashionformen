package com.example.ecommerce_fashionformen.dto.user;


import com.example.ecommerce_fashionformen.controllers.common.validation.MinAge;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
public class UserRegisterRequest {

    @NotBlank(message = "Tên đăng nhập không được để trống và chứa khoảng trắng")
    @Size(min = 4, max = 50, message = "Tên đăng nhập phải từ 4 đến 50 ký tự")
    private String username;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, max = 32, message = "Mật khẩu phải từ 6 đến 32 ký tự")
    private String password;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    @Size(max = 100, message = "Email tối đa 100 ký tự")
    private String email;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^(0[3|5|7|8|9])+([0-9]{8})$", message = "Số điện thoại không hợp lệ")
    private String phone;

    @NotBlank(message = "Họ và tên không được để trống")
    @Size(max = 255, message = "Họ và tên tối đa 255 ký tự")
    private String fullName;

    @NotNull(message = "Ngày sinh không được để trống")
    @MinAge(value = 13, message = "Bạn phải từ 13 tuổi trở lên mới được mua hàng")
    private LocalDate dateOfBirth;


}
