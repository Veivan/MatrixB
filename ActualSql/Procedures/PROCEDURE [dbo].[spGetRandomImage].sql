SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:	Vetrov
-- Description:	Select random image from DB
-- @gender = 0 (FEMALE) or = 1 (MALE) or = null (NEUTRAL)
-- @ptype_id is link to DicPicType
-- =============================================
ALTER PROCEDURE [dbo].[spGetRandomImage]
	@gender BIT
	,@ptype_id INT
	,@pic VARBINARY(MAX) OUTPUT
AS
BEGIN
	SET NOCOUNT ON;	
	CREATE TABLE #tmp(nnid INT IDENTITY(1,1), [pic_id] INT)
	DECLARE @randomnum INT, @reccnt INT, @pic_id INT, @selid INT

	INSERT INTO #tmp ([pic_id])
	SELECT [pic_id] FROM [dbo].[mPicture] (tablockx)
	WHERE 
		[ptype_id] = @ptype_id 
		AND ISNULL([isused], 0) = 0
		AND
			((@gender IS NULL AND [gender] IS NULL)
			OR
			(@gender IS NOT NULL AND [gender] = @gender))

	SET @reccnt = @@ROWCOUNT
	SET @randomnum = Ceiling(Rand() * @reccnt)

	SELECT @selid = [pic_id]
	FROM #tmp
	WHERE [nnid] = @randomnum

	UPDATE [dbo].[mPicture] SET [isused] = 1
	WHERE [pic_id] = @selid
 
	SELECT @pic = [fpicture], @pic_id = P.[pic_id] FROM [dbo].[mPicture] P
	WHERE [pic_id] = @selid

	--PRINT @pic_id
END
GO
