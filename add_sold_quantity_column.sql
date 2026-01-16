-- Migration: Thêm cột sold_quantity vào bảng product
-- Mô tả: Thêm cột để lưu số lượng sản phẩm đã bán
-- SQL Server syntax

ALTER TABLE product
ADD sold_quantity INT NOT NULL DEFAULT 0;

-- Cập nhật giá trị mặc định cho các sản phẩm hiện có (nếu cần)
-- UPDATE product SET sold_quantity = 0 WHERE sold_quantity IS NULL;
