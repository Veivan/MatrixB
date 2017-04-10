SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
ALTER PROCEDURE [dbo].[isRetweetedByUser]
	@tw_id BIGINT
	,@user_id BIGINT
AS BEGIN
	SET NOCOUNT ON;
	DECLARE @result BIT
	SET @result = 0
	SELECT * 
	FROM [dbo].[twTwits] T
		INNER JOIN [dbo].[mAccounts] A ON A.[twitter_id] = T.[creator_id] AND A.[user_id] = @user_id
	WHERE 
		T.[retweeted_id] = @tw_id
		
	IF @@ROWCOUNT > 0
		SET @result = 1
	SELECT @result as result
END
GO
