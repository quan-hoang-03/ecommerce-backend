-- ============================================
-- SQL QUERIES TO UPDATE PRODUCT QUANTITY
-- ============================================

-- ============================================
-- UPDATE CHO TẤT CẢ SẢN PHẨM
-- ============================================

-- 1. Update số lượng tổng cho TẤT CẢ sản phẩm
UPDATE Product 
SET quantity = 100;  -- Thay đổi giá trị này cho tất cả sản phẩm

-- 2. Update số lượng đã bán cho TẤT CẢ sản phẩm
UPDATE Product 
SET sold_quantity = 0;  -- Reset số lượng đã bán về 0 cho tất cả

-- 3. Reset cả số lượng tổng và số lượng đã bán cho TẤT CẢ sản phẩm
UPDATE Product 
SET quantity = 100,
    sold_quantity = 0;

-- 4. Tăng số lượng tổng thêm một giá trị cho TẤT CẢ sản phẩm
UPDATE Product 
SET quantity = quantity + 50;  -- Tăng thêm 50 cho tất cả

-- 5. Giảm số lượng tổng đi một giá trị cho TẤT CẢ sản phẩm
UPDATE Product 
SET quantity = quantity - 10;  -- Giảm đi 10 cho tất cả

-- 6. Set số lượng tổng = số lượng đã bán + một giá trị cố định cho TẤT CẢ
UPDATE Product 
SET quantity = sold_quantity + 100;  -- Số lượng tổng = đã bán + 100

-- 7. Reset số lượng đã bán về 0 cho TẤT CẢ sản phẩm (giữ nguyên số lượng tổng)
UPDATE Product 
SET sold_quantity = 0;

-- 8. Đảm bảo số lượng tổng >= số lượng đã bán cho TẤT CẢ sản phẩm
UPDATE Product 
SET quantity = CASE 
    WHEN quantity < sold_quantity THEN sold_quantity + 50
    ELSE quantity
END;

-- ============================================
-- UPDATE CHO SẢN PHẨM CỤ THỂ
-- ============================================

-- 1. Update số lượng tổng (quantity) của một sản phẩm cụ thể theo ID
UPDATE Product 
SET quantity = 100  -- Thay đổi giá trị này
WHERE id = 1;       -- Thay đổi ID sản phẩm

-- 2. Update số lượng đã bán (sold_quantity) của một sản phẩm cụ thể theo ID
UPDATE Product 
SET sold_quantity = 50  -- Thay đổi giá trị này
WHERE id = 1;           -- Thay đổi ID sản phẩm

-- 3. Update cả số lượng tổng và số lượng đã bán cùng lúc
UPDATE Product 
SET quantity = 100,
    sold_quantity = 50
WHERE id = 1;

-- 4. Update số lượng tổng theo tên sản phẩm (title)
UPDATE Product 
SET quantity = 100
WHERE title LIKE '%Son Tint%';  -- Thay đổi tên sản phẩm

-- 5. Update số lượng tổng theo category
UPDATE Product 
SET quantity = 100
WHERE category_id = 1;  -- Thay đổi ID category

-- 6. Update số lượng tổng cho tất cả sản phẩm có số lượng < 10 (hàng sắp hết)
UPDATE Product 
SET quantity = 50
WHERE quantity < 10;

-- 7. Giảm số lượng tổng đi một số lượng cụ thể (ví dụ: sau khi bán)
UPDATE Product 
SET quantity = quantity - 5  -- Giảm đi 5
WHERE id = 1;

-- 8. Tăng số lượng đã bán và giảm số lượng tổng cùng lúc (khi có đơn hàng)
UPDATE Product 
SET quantity = quantity - 3,      -- Giảm số lượng tổng đi 3
    sold_quantity = sold_quantity + 3  -- Tăng số lượng đã bán lên 3
WHERE id = 1;

-- 9. Update số lượng tổng dựa trên số lượng còn lại (quantity - sold_quantity)
UPDATE Product 
SET quantity = sold_quantity + 50  -- Đặt số lượng tổng = số đã bán + 50
WHERE id = 1;

-- 10. Update số lượng tổng cho nhiều sản phẩm cùng lúc
UPDATE Product 
SET quantity = CASE 
    WHEN id = 1 THEN 100
    WHEN id = 2 THEN 200
    WHEN id = 3 THEN 150
    ELSE quantity
END
WHERE id IN (1, 2, 3);

-- ============================================
-- QUERIES HỮU ÍCH KHÁC
-- ============================================

-- Xem số lượng hiện tại của tất cả sản phẩm
SELECT id, title, quantity, sold_quantity, (quantity - sold_quantity) AS available_quantity
FROM Product
ORDER BY id;

-- Xem các sản phẩm sắp hết hàng (số lượng còn lại < 10)
SELECT id, title, quantity, sold_quantity, (quantity - sold_quantity) AS available_quantity
FROM Product
WHERE (quantity - sold_quantity) < 10;

-- Xem các sản phẩm đã hết hàng
SELECT id, title, quantity, sold_quantity
FROM Product
WHERE quantity <= sold_quantity OR quantity = 0;
