# Hướng dẫn Fix Lỗi Font (Encoding) cho Database

## Vấn đề
Các text tiếng Việt bị lỗi font khi lưu vào database vì các columns đang sử dụng `VARCHAR` thay vì `NVARCHAR`.

## Giải pháp

### Bước 1: Chạy Script SQL
1. Mở **SQL Server Management Studio** hoặc **Azure Data Studio**
2. Kết nối đến database `ecommerce-data`
3. Mở file `fix_encoding_columns.sql` trong thư mục project
4. Chạy script này để chuyển các columns từ `VARCHAR` sang `NVARCHAR(MAX)`

Script sẽ update các columns sau:
- `title`
- `brand`
- `colors`
- `description`

### Bước 2: Restart Backend
Sau khi chạy script SQL, restart lại Spring Boot application.

### Bước 3: Test lại
1. Tạo hoặc update một sản phẩm với text tiếng Việt
2. Kiểm tra trong database xem text có hiển thị đúng không
3. Kiểm tra trên frontend xem text có hiển thị đúng không

## Lưu ý
- Script SQL sẽ không mất dữ liệu hiện có
- Nếu có dữ liệu cũ bị lỗi font, bạn có thể cần update lại các records đó
- Đảm bảo database collation hỗ trợ Unicode (SQL_Latin1_General_CP1_CI_AS hoặc Vietnamese_CI_AS)

## Kiểm tra sau khi fix
Chạy query sau để kiểm tra column types:
```sql
SELECT 
    COLUMN_NAME,
    DATA_TYPE,
    CHARACTER_MAXIMUM_LENGTH
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME = 'product' 
    AND COLUMN_NAME IN ('title', 'brand', 'colors', 'description')
```

Kết quả mong đợi: `DATA_TYPE` phải là `nvarchar` (không phải `varchar`).
