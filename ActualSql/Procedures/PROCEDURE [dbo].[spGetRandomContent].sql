SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:	Vetrov
-- Description:	Select random content for twit from DB
-- =============================================
ALTER PROCEDURE [dbo].[spGetRandomContent]
	@twit_id INT
AS
BEGIN
	SET NOCOUNT ON;	
	DECLARE @tmp TABLE(nnid INT IDENTITY(1,1), [rnt_id] BIGINT)
	DECLARE @randomnum INT, @reccnt INT, @rnt_id BIGINT

	INSERT INTO @tmp ([rnt_id])
	SELECT [rnt_id] 
	FROM [dbo].[mRandText] 	WHERE [twit_id] = @twit_id 

	SET @reccnt = @@ROWCOUNT
	SET @randomnum = Ceiling(Rand() * @reccnt)
 
	SELECT @rnt_id = [rnt_id] FROM @tmp 
	WHERE [nnid] = @randomnum

	SELECT [randtext]
      ,[fpicture]
      ,[url]
	  ,[urlshort]
	FROM [dbo].[mRandText] R
		INNER JOIN @tmp T ON T.[rnt_id] = R.[rnt_id]
		LEFT JOIN [dbo].[mPicture] P ON P.[pic_id] = R.[pic_id]
	WHERE T.[nnid] = @randomnum 

END
GO
