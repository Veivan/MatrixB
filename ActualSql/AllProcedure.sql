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
		AND P.[alive] = 1
END
GO

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- ================================================
-- Author:	Vetrov
-- Description:	Setting proxy for account
-- ================================================
ALTER PROCEDURE [dbo].[spProxy4AccUpdate]
	@user_id INT
	,@ProxyID INT 
AS BEGIN
	SET NOCOUNT ON;

	IF (@ProxyID = 0)
		DELETE [dbo].[mProxyAcc] WHERE [user_id] = @user_id
	ELSE
		MERGE [dbo].[mProxyAcc] PA
		USING (SELECT [user_id] = @user_id ) I 
			ON PA.[user_id] = I.[user_id]
		WHEN NOT MATCHED BY TARGET THEN INSERT
			([user_id], [ProxyID])
		VALUES
			(@user_id, @ProxyID)
		WHEN MATCHED THEN UPDATE SET
			[ProxyID] = @ProxyID
		;
END
GO

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- ================================================
-- Author:	Vetrov
-- Description:	Selecting free proxies
-- ================================================
ALTER PROCEDURE [dbo].[spProxyFreeSelect]
AS BEGIN
	SET NOCOUNT ON;
	SELECT TOP 5
		PA.[acprID]
		,PA.[user_id]
		,P.[ProxyID]
		,P.[ip]
		,P.[port]
		,P.[prtypeID]
		,[typename] = LTRIM(RTRIM(D.[typename]))
	FROM [dbo].[mProxies] P
		LEFT JOIN [dbo].[mProxyAcc] PA ON P.[ProxyID] = PA.[ProxyID]
		JOIN [dbo].[DicProxyType] D ON P.[prtypeID] = D.[prtypeID]
	WHERE 
		P.[alive] = 1
		AND PA.[acprID] IS NULL
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

