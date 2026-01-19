-- Script để fix encoding cho các columns trong bảng product
-- Chạy script này trong SQL Server Management Studio hoặc Azure Data Studio

USE [ecommerce-data];
GO

-- Kiểm tra và alter column title
IF EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'[dbo].[product]') AND name = 'title')
BEGIN
    ALTER TABLE [dbo].[product] 
    ALTER COLUMN [title] NVARCHAR(MAX) COLLATE SQL_Latin1_General_CP1_CI_AS;
    PRINT 'Column title đã được update thành NVARCHAR(MAX)';
END
ELSE
BEGIN
    PRINT 'Column title không tồn tại';
END
GO

-- Kiểm tra và alter column brand
IF EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'[dbo].[product]') AND name = 'brand')
BEGIN
    ALTER TABLE [dbo].[product] 
    ALTER COLUMN [brand] NVARCHAR(MAX) COLLATE SQL_Latin1_General_CP1_CI_AS;
    PRINT 'Column brand đã được update thành NVARCHAR(MAX)';
END
ELSE
BEGIN
    PRINT 'Column brand không tồn tại';
END
GO

-- Kiểm tra và alter column colors
IF EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'[dbo].[product]') AND name = 'colors')
BEGIN
    ALTER TABLE [dbo].[product] 
    ALTER COLUMN [colors] NVARCHAR(MAX) COLLATE SQL_Latin1_General_CP1_CI_AS;
    PRINT 'Column colors đã được update thành NVARCHAR(MAX)';
END
ELSE
BEGIN
    PRINT 'Column colors không tồn tại';
END
GO

-- Kiểm tra và alter column description (nếu chưa phải NVARCHAR)
IF EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'[dbo].[product]') AND name = 'description')
BEGIN
    ALTER TABLE [dbo].[product] 
    ALTER COLUMN [description] NVARCHAR(MAX) COLLATE SQL_Latin1_General_CP1_CI_AS;
    PRINT 'Column description đã được update thành NVARCHAR(MAX)';
END
ELSE
BEGIN
    PRINT 'Column description không tồn tại';
END
GO

PRINT 'Hoàn thành! Tất cả các columns đã được chuyển sang NVARCHAR(MAX) để hỗ trợ Unicode.';
GO
