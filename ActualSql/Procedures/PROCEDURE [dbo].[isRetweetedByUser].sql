SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- ================================================
-- Author:	Vetrov
-- Description:	Check if Status retweeted by user
-- ================================================
ALTER PROCEDURE [dbo].[isRetweetedByUser]
	@tw_id BIGINT
	,@user_id BIGINT
AS BEGIN
	SET NOCOUNT ON;
	DECLARE @result BIT
	SET @result = 0
	SELECT * 
	FROM [dbo].[twTwits] 
	WHERE [retweeted_id] = @tw_id
		AND [creator_id] = @user_id
	IF @@ROWCOUNT > 0
		SET @result = 1
	SELECT @result as result
END
GO
