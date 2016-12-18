SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- ================================================
-- Author:	Vetrov
-- Description:	Selecting credentials
-- ================================================
ALTER PROCEDURE [dbo].[spCredsSelect]
	@user_id INT
AS BEGIN
	SET NOCOUNT ON;
	SELECT TOP 1
		U.[user_id]
		,U.[name]
		,U.[pass]
		,T.[token]
		,T.[token_secret]
		,A.[cons_key]
		,A.[cons_secret]
	FROM [dbo].[mAccounts] U
		JOIN [dbo].[mTokens] T ON T.[user_id] = U.[user_id]
		JOIN [dbo].[mApplications] A ON A.[id_app] = T.[id_app]
	WHERE 
		U.[user_id] = @user_id
END
GO

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- ================================================
-- Author:	Vetrov
-- Description:	Selecting proxy 4 account
-- ================================================
ALTER PROCEDURE [dbo].[spProxy4AccSelect]
	@user_id int
AS BEGIN
	SET NOCOUNT ON;

	SELECT TOP 1
		PA.[acprID]
		,PA.[user_id]
		,PA.[ProxyID]
		,P.[ip]
		,P.[port]
		,P.[prtypeID]
		,[typename] = LTRIM(RTRIM(D.[typename]))
	FROM [dbo].[mProxyAcc] PA
		JOIN [dbo].[mProxies] P ON P.[ProxyID] = PA.[ProxyID]
		JOIN [dbo].[DicProxyType] D ON P.[prtypeID] = D.[prtypeID]
	WHERE 
		PA.[user_id] = @user_id
END
GO

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- ================================================
-- Author:	Vetrov
-- Description:	Selecting tasks
-- ================================================
ALTER PROCEDURE [dbo].[spTasksSelect]
	@TDate DATETIME = NULL
AS BEGIN
	SET NOCOUNT ON;
	SELECT 
		T.[id_Task]
		,T.[TaskDate]
		,T.[id_TaskType]
		,T.[TContent]
		,T.[IsRepeat]
		,D.[TypeMean]
	FROM [dbo].[mTasks] T
		LEFT JOIN [dbo].[DicTaskType] D ON D.[id_TaskType] = T.[id_TaskType]
	WHERE 
		T.[TaskDate] = @TDate
		OR
		T.[IsRepeat] = 1
	ORDER BY T.[id_Task]
END
GO

