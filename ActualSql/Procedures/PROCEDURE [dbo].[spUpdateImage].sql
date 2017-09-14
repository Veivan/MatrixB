SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:	Vetrov
-- Description:	Update image from binary array
-- @gender = 0 (FEMALE) or = 1 (MALE) or = null (NEUTRAL)
-- @ptype_id is link to DicPicType
-- =============================================
ALTER PROCEDURE [dbo].[spUpdateImage]
	@pic_id INT
	,@pic VARBINARY(MAX)
	,@gender BIT
	,@ptype_id INT
AS
BEGIN
	SET NOCOUNT ON;	

	MERGE [dbo].[mPicture] P
		USING (SELECT [pic_id] = @pic_id) I 
			ON P.[pic_id] = I.[pic_id] 
		WHEN NOT MATCHED BY TARGET THEN 
			INSERT ([fpicture], [gender], [ptype_id])
			VALUES (@pic, @gender, @ptype_id)
		WHEN MATCHED THEN UPDATE SET
			[fpicture] = @pic
			,[gender] = @gender
			,[ptype_id] = @ptype_id		
		;	
END
GO
