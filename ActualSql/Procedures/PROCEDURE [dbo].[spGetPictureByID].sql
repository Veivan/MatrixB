SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:	Vetrov
-- Description:	Select picture from DB
-- @pic_id - id from mPicture
-- image ptype_id is 3 (PIC4TWIT)
-- =============================================
ALTER PROCEDURE [dbo].[spGetPictureByID]
	@pic_id INT
	,@pic VARBINARY(MAX) OUTPUT
AS
BEGIN
	SET NOCOUNT ON;	
 
	SELECT @pic = [fpicture] FROM [dbo].[mPicture] P
	WHERE [pic_id] = @pic_id

	--PRINT @pic_id
END
GO
