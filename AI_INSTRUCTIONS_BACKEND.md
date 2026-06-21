GIAO THỨC LÀM VIỆC VÀ CẤU HÌNH DỰ ÁN TOÀN DIỆN (SYSTEM INSTRUCTIONS)
Tài liệu này là bộ nguyên tắc thiết kế, cấu trúc thư mục, quy tắc viết code và giao thức làm việc dành cho AI khi phát triển dự án Fullstack kết hợp giữa Spring Boot (Backend) và React / Vite / TypeScript (Frontend). Bộ cấu hình này được thiết kế để tái sử dụng làm chuẩn mực (template) chung cho mọi dự án phần mềm của tôi.

PHẦN A: KIẾN TRÚC VÀ QUY TẮC BACKEND (SPRING BOOT)

1. Kiến trúc chung (Architecture)
   Dự án được triển khai theo kiến trúc phân lớp (Layered Architecture) chuẩn mực với sự tách biệt rõ ràng giữa các phần trách nhiệm:

Entity: Lớp biểu diễn database thực thể JPA.

Repository: Lớp giao tiếp với Database sử dụng Spring Data JPA.

DTO: Lớp trung chuyển dữ liệu giữa các API Request/Response và Service, cô lập Entity khỏi lớp Controller.

Service (Interface & Impl): Lớp xử lý Logic nghiệp vụ cốt lõi, quản trị Transaction.

Controller: Lớp định nghĩa API RESTful đầu cuối, xử lý Routing và Validation.

Security & Config: Lớp cấu hình Spring Security, JWT và các thư viện hỗ trợ (ModelMapper, OpenAPI).

Common & Exception: Hệ thống quản trị ngoại lệ tập trung và cấu trúc dữ liệu phản hồi API thống nhất.

2. Chuẩn hóa Định dạng API & Quản lý Exception (Exception & API Format)
   2.1. API Response Format chuẩn
   Mọi API phản hồi phải nằm trong một bản ghi (Record) chuẩn hóa duy nhất:

Java

public record ApiResponse<T>(boolean success, String message, T data, LocalDateTime timestamp) {
public static <T> ApiResponse<T> success(String message, T data) {
return new ApiResponse<>(true, message, data, LocalDateTime.now());
}

    public static <T> ApiResponse<T> success(T data) {
        return success("Thao tác thành công", data);
    }

    public static ApiResponse<Void> successMessage(String message) {
        return success(message, null);
    }

    public static <T> ApiResponse<T> error(String message, T data) {
        return new ApiResponse<>(false, message, data, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null, LocalDateTime.now());
    }

    public static ApiResponse<Void> fail() {
        return error("Đã xảy ra sự cố không xác định", null);
    }

}
2.2. Xử lý Exception tập trung (Global Exception Handler)
Tất cả các exception trong ứng dụng phải được bắt giữ và định dạng lại qua bộ điều khiển tập trung @RestControllerAdvice.
Dưới đây là thiết lập chuẩn:

NotFoundException (Runtime): Phản hồi HTTP 404 Not Found.

BadRequestException (Runtime) & ConstraintViolationException: Phản hồi HTTP 400 Bad Request.

MethodArgumentNotValidException (Lỗi Validator): Phản hồi HTTP 400 Bad Request kèm một danh sách chi tiết các trường bị lỗi Map<String, String>.

BadCredentialsException: Phản hồi HTTP 401 Unauthorized.

AccessDeniedException: Phản hồi HTTP 403 Forbidden.

DataIntegrityViolationException (Lỗi ràng buộc DB): Phản hồi HTTP 400 Bad Request kèm chi tiết lỗi SQL cụ thể.

Exception (Unknown errors): Ghi log hệ thống và trả về HTTP 500 Internal Server Error ẩn thông tin nhạy cảm.

Mã nguồn GlobalExceptionHandler mẫu:

Java

import com.yo.day1.common.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler({BadRequestException.class, ConstraintViolationException.class})
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(Exception ex) {
        return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(ApiResponse.error("Dữ liệu đầu vào không hợp lệ", errors));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentials(BadCredentialsException ex) {
        // Có thể thay thế ex.getMessage() nếu muốn đồng nhất tiếng Việt hoàn toàn: "Tài khoản hoặc mật khẩu không chính xác"
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("Bạn không có quyền truy cập vào chức năng này"));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrity(DataIntegrityViolationException ex) {
        String details = ex.getMostSpecificCause().getMessage();
        return ResponseEntity.badRequest().body(ApiResponse.error("Lỗi dữ liệu (Trùng lặp hoặc vi phạm ràng buộc hệ thống): " + details));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnknown(Exception ex) {
        log.error("Unhandled application error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Lỗi hệ thống máy chủ nội bộ. Vui lòng liên hệ quản trị viên. Chi tiết: " + ex.getMessage()));
    }

}

3. Quy tắc Thiết kế Thực thể (Entity Design Rules)
   Base & Audit: Mọi thực thể nên kế thừa từ BaseEntity (nếu chỉ cần ID tự tăng) hoặc AuditableEntity (nếu cần tự động ghi nhận thời gian khởi tạo/cập nhật).

Tránh tự viết mã trường ID tự tăng trong từng thực thể.

Lombok:

Lombok trên Entity:

Sử dụng @Getter và @Setter trên mức Class.

Bắt buộc sử dụng @NoArgsConstructor để đảm bảo Hibernate có constructor không tham số nhằm khởi tạo thực thể khi truy vấn dữ liệu.

TUYỆT ĐỐI KHÔNG sử dụng @Data trên các thực thể JPA....

Sử dụng @EqualsAndHashCode(callSuper = false) khi kế thừa các Base Class.

Ràng buộc Database:

Khai báo rõ ràng thuộc tính @Column gồm name, nullable, unique, và length (ví dụ: @Column(name = "full_name", nullable = false, length = 100)).

Đối với kiểu số thực dấu phẩy động (như float, double dành cho điểm số, tiền tệ, phần trăm giảm giá...): KHÔNG ĐƯỢC chỉ định precision hoặc scale trong @Column để tránh lỗi khởi tạo Hibernate: scale has no meaning for SQL floating point types.

Quan hệ giữa các thực thể:

Ưu tiên sử dụng @ManyToOne(fetch = FetchType.LAZY) kết hợp với @JoinColumn(name = "xxx_id") để tối ưu hóa hiệu năng truy vấn database, tránh Fetch Eager ngầm định của Hibernate gây ra lỗi N+1 queries.

Kiểu Enum:

Dùng @Enumerated(EnumType.STRING) để lưu Enum dưới dạng chuỗi rõ nghĩa trong cơ sở dữ liệu. Khai báo độ dài cột rõ ràng trong @Column(length = ...).

Ví dụ thực thể chuẩn:

Java
@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@Table(name = "students")
public class Student extends AuditableEntity {

    @Column(name = "student_code", nullable = false, unique = true, length = 20)
    private String studentCode;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Gender gender = Gender.OTHER;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Parent parent;

    @Column(name = "latest_score")
    private float latestScore; // Dùng kiểu nguyên thủy float thay vì BigDecimal, không chỉ định scale/precision

}

4. Thiết kế Lớp DTO (DTO Design Rules)
   Phân tách rõ ràng: Tách biệt hoàn toàn DTO gửi lên theo từng hành động nghiệp vụ cụ thể (Request) và dữ liệu trả về (Response). Quy tắc đặt tên và phân tách dựa trên nghiệp vụ như sau:

Create Request: Đặt tên dạng [Entity]CreateRequest. Phục vụ cho hành động POST (Tạo mới), bắt buộc nhập đầy đủ các trường cốt lõi để khởi tạo đối tượng.

Update Request: Đặt tên dạng [Entity]UpdateRequest. Ưu tiên phục vụ cho hành động PATCH (Cập nhật một phần), loại bỏ các trường bất biến (ví dụ: studentCode) và chỉ gửi lên những trường cần thay đổi để tránh ghi đè dữ liệu rác.

Upsert Request (Trường hợp gộp chung): Chỉ sử dụng tên dạng [Entity]UpsertRequest khi cấu trúc dữ liệu, các trường cần tác động và logic validation của cả hai hành động Tạo mới & Cập nhật hoàn toàn trùng khớp và chung nhau, không có trường bất biến cần loại bỏ.

Response: Phục vụ cho dữ liệu trả về.

Validation trên Request:

Áp dụng các annotation validation chuẩn của Jakarta Bean Validation để chặn dữ liệu rác ngay tại Controller.

Sử dụng @NotNull, @NotBlank, @Size, @Pattern, @Min, @Max, @Email.

Lombok trên DTO:

Có thể sử dụng @Data, @AllArgsConstructor, @NoArgsConstructor một cách an toàn trên DTO do không chịu sự quản lý trực tiếp của JPA Hibernate.

Relational mapping:

Response DTO nên trả ra ID của liên kết (Long parentId) và có thể kèm cả Response DTO con (ParentResponse parent) nếu giao diện cần hiển thị trực tiếp thông tin liên quan.

Chứa cả các thuộc tính audit như createdAt và updatedAt.

Ví dụ Create Request DTO:
Java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentCreateRequest {
@NotBlank
@Size(min = 2, max = 10)
private String studentCode; // Bắt buộc khi tạo mới

    @NotBlank
    @Size(min = 2, max = 100)
    private String fullName;

    @NotNull
    private Gender gender;

    @Pattern(regexp = "^(84|0[35789])+([0-9]{8})$", message = "Số điện thoại không hợp lệ")
    private String phone;

    private Long parentId;

    @Min(0) @Max(10)
    private float latestScore;

}
Ví dụ Update Request DTO (Dành cho PATCH):
Java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentUpdateRequest {
// Không chứa studentCode vì đây là trường bất biến
// Client chỉ truyền lên các trường mong muốn thay đổi

    @NotBlank
    @Size(min = 2, max = 100)
    private String fullName;

    @NotNull
    private Gender gender;

    @Pattern(regexp = "^(84|0[35789])+([0-9]{8})$", message = "Số điện thoại không hợp lệ")
    private String phone;

    private Long parentId;

    @Min(0) @Max(10)
    private float latestScore;

}
Ví dụ Response DTO:
Java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentResponse {
private Long id;
private String studentCode;
private String fullName;
private Gender gender;
private Long parentId;
private ParentResponse parent;
private float latestScore;
private LocalDateTime createdAt;
private LocalDateTime updatedAt;
}

5. Thiết kế Lớp Nghiệp vụ (Service Design Rules)
   Tính trừu tượng: Sử dụng thiết kế Interface trước (ví dụ StudentService) và lớp triển khai sau (ví dụ StudentServiceImpl).

Dependency Injection:

Ưu tiên constructor injection tự sinh bằng Lombok @RequiredArgsConstructor trên lớp Implementation.

Sử dụng từ khóa private final đối với mọi Bean được tiêm vào để đảm bảo tính bất biến (Immutability).

Quản lý Giao dịch (Transactions):

Khai báo @Transactional rõ ràng.

Sử dụng @Transactional(readOnly = true) trên các phương thức hoặc lớp chỉ truy vấn dữ liệu nhằm nâng cao hiệu năng đọc.

Sử dụng @Transactional mặc định đối với các thao tác thay đổi dữ liệu (Create, Update, Delete).

Chuyển đổi Entity ↔ DTO:

Sử dụng thư viện ModelMapper (cấu hình STRICT matching strategy để tránh ánh xạ nhầm lẫn thuộc tính cùng tên).

Kết hợp bổ sung ánh xạ thủ công (Custom Mapping) đối với các quan hệ thực thể phức tạp hoặc khóa ngoại.

Ví dụ cấu trúc triển khai Service:

Java
@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {
private final StudentRepository studentRepository;
private final ParentRepository parentRepository;
private final ModelMapper mapper;

    private StudentResponse mapToResponse(Student student) {
        StudentResponse res = mapper.map(student, StudentResponse.class);
        if (student.getParent() != null) {
            res.setParentId(student.getParent().getId());
            res.setParent(mapper.map(student.getParent(), ParentResponse.class));
        }
        return res;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentResponse> findAll() {
        return studentRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public StudentResponse create(StudentUpsertRequest req) {
        Student stu = mapper.map(req, Student.class);
        parentRepository.findById(req.getParentId()).ifPresent(stu::setParent);
        return mapToResponse(studentRepository.save(stu));
    }

} 6. Thiết kế Lớp API Endpoint (Controller Design Rules)
Annotations chuẩn: @RestController, @RequiredArgsConstructor, và @RequestMapping("/api/students") để thiết lập đường dẫn gốc rõ ràng.

Kiểu dữ liệu trả về: Bắt buộc là ApiResponse<T> để đồng nhất cấu trúc dữ liệu cho toàn bộ frontend xử lý.

Validation: Đặt @Valid trước @RequestBody để Spring tự động kích hoạt bộ kiểm tra ràng buộc DTO.

Rõ nghĩa HTTP Method:

GET : Lấy thông tin (danh sách hoặc đơn lẻ).

POST : Khởi tạo bản ghi mới.

PUT : Thay đổi toàn diện hoặc cập nhật dữ liệu.

DELETE : Xóa dữ liệu.

Ví dụ REST Controller:

Java
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/students")
public class StudentController {
private final StudentService studentService;

    @GetMapping
    public ApiResponse<List<StudentResponse>> getStudents() {
        return ApiResponse.success("Lấy danh sách học sinh thành công", studentService.findAll());
    }

    @PostMapping
    public ApiResponse<StudentResponse> create(@Valid @RequestBody StudentUpsertRequest req) {
        return ApiResponse.success("Tạo học sinh thành công", studentService.create(req));
    }

} 7. Hệ thống Security & JWT
Cấu hình Spring Security stateless (SessionCreationPolicy.STATELESS), tắt CSRF, cấu hình CORS mặc định.

Cho phép truy cập công khai vào /api/auth/login, /api/auth/refresh, tài liệu OpenAPI/Swagger (/v3/api-docs/, /swagger-ui/).

Các API nghiệp vụ còn lại bắt buộc authenticate bằng Access Token đính trong HTTP Header: Authorization: Bearer <accessToken>.

Bộ lọc JwtAuthenticationFilter chịu trách nhiệm chặn mọi request, kiểm tra định dạng JWT, trích xuất Username và phân quyền (Roles) gán tiền tố ROLE\_ trước khi đưa vào SecurityContextHolder.

Các trường hợp thất bại (token hết hạn, không hợp lệ) phải phản hồi lỗi chuẩn kiểu JSON ApiResponse dạng 401 Unauthorized trực tiếp thông qua luồng Response Servlet Writer.
