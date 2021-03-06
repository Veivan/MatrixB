SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:	Vetrov
-- Description:	Select random screenname DB
-- =============================================
ALTER PROCEDURE [dbo].[spGetRandomScreenName]
	@user_id BIGINT
AS
BEGIN
	SET NOCOUNT ON;	
	CREATE TABLE #tmp(nnid INT IDENTITY(1,1), [screen_name] NVARCHAR(150), [twitter_id] BIGINT)
	DECLARE @randomnum INT, @reccnt INT

	INSERT INTO #tmp ([screen_name], [twitter_id])
	SELECT A.[screen_name], A.[twitter_id] 
	FROM [dbo].[mAccounts] A
		LEFT JOIN [dbo].[mFollowInfo] F ON 
			F.[twitter_id] = A.[twitter_id] AND F.[user_id] = @user_id AND F.[fwtype] = 1 --friend
	WHERE A.[enabled] = 1 
		AND A.[user_id] != @user_id
		AND A.[twitter_id] IS NOT NULL
		AND F.fw_id IS NULL

	SET @reccnt = @@ROWCOUNT
	SET @randomnum = Ceiling(Rand() * @reccnt)
 
	SELECT [screen_name], [twitter_id] FROM #tmp 
	WHERE [nnid] = @randomnum
END
GO
