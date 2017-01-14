SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
ALTER PROCEDURE [dbo].[spLoadImage]
	@pic VARBINARY(MAX)
	,@gender BIT
	,@ptype_id INT
AS
BEGIN
	SET NOCOUNT ON;	
	INSERT INTO [dbo].[mPicture] (fpicture, gender, ptype_id) VALUES (@pic, @gender, @ptype_id)
END
GO
