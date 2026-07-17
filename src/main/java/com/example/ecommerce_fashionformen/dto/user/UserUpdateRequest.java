package com.example.ecommerce_fashionformen.dto.user;

import com.example.ecommerce_fashionformen.controllers.common.validation.MinAge;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Setter
@Getter
public class UserUpdateRequest {

    @Size(max = 255, message = "Họ và tên tối đa 255 ký tự")
    private String fullName;


    @Email(message = "Email không đúng định dạng")
    @Size(max = 100, message = "Email tối đa 100 ký tự")
    private String email;


    @Pattern(regexp = "^(0[3|5|7|8|9])+([0-9]{8})$", message = "Số điện thoại không hợp lệ")
    private String phone;

    @Size(max = 500, message = "Đường dẫn avatar quá dài")
    private String avatarUrl;

   @MinAge(value = 13, message = "Bạn phải từ 13 tuổi trở lên mới được mua hàng")
    private LocalDate dateOfBirth;
}
