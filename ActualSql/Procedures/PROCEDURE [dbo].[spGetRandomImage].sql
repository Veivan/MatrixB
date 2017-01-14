SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
ALTER PROCEDURE [dbo].[spGetRandomImage]
	@gender BIT
	,@ptype_id INT
	,@pic VARBINARY(MAX) OUTPUT
AS
BEGIN
	SET NOCOUNT ON;	
	CREATE TABLE #tmp(nnid INT IDENTITY(1,1), [pic_id] INT)
	DECLARE @randomnum INT, @reccnt INT, @pic_id INT

	INSERT INTO #tmp ([pic_id])
	SELECT [pic_id] FROM [dbo].[mPicture]
	WHERE [ptype_id] = @ptype_id AND
	((@gender IS NULL AND [gender] IS NULL)
	OR
	(@gender IS NOT NULL AND [gender] = @gender))

	SET @reccnt = @@ROWCOUNT
	SET @randomnum = Ceiling(Rand() * @reccnt)
 
	SELECT @pic = [fpicture], @pic_id = P.[pic_id] FROM [dbo].[mPicture] P
		INNER JOIN #tmp T ON T.[pic_id] = P.[pic_id]
	WHERE [nnid] = @randomnum

	--PRINT @pic_id
END
GO
