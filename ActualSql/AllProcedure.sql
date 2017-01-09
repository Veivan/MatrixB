SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:	Vetrov
-- Description:	Add or update account
-- =============================================
ALTER PROCEDURE [dbo].[spAccountAdd]
	@user_id BIGINT OUTPUT,
	@name NVARCHAR(50),  
	@screen_name NVARCHAR(150),
	@email NVARCHAR(50),  
	@phone NVARCHAR(50),  
	@pass NVARCHAR(50),  
	@twitter_id BIGINT,
	@group_id INT,
	@mailpass NVARCHAR(50) 
AS BEGIN
	SET NOCOUNT ON

	IF @screen_name IS NULL SET @screen_name = @name
	IF @twitter_id = -1 SET @twitter_id = NULL

	IF (@user_id = -1) BEGIN
		SET @user_id = NULL
		SELECT @user_id = [user_id] FROM [dbo].[mAccounts]
		WHERE [name] = @name AND [email] = @email AND [pass] = @pass
	END

	IF (@user_id IS NULL) BEGIN
		INSERT INTO [dbo].[mAccounts]
			   ([name]
			   ,[screen_name]
			   ,[email]
			   ,[phone]
			   ,[pass]
			   ,[twitter_id]
			   ,[mailpass]
			   ,[finsert])
		 VALUES
			   (@name, @screen_name, @email, @phone, @pass, @twitter_id, @mailpass, GETDATE())
		SET @user_id = @@IDENTITY
	END
	ELSE 
		UPDATE [dbo].[mAccounts]
		   SET [name] = @name
			  ,[screen_name] = @screen_name
			  ,[email] = @email
			  ,[phone] = @phone
			  ,[pass] = @pass
			  ,[twitter_id] = @twitter_id
			  ,[mailpass] = @mailpass
		 WHERE [user_id] = @user_id

	MERGE [dbo].[mBelong2] B
	USING (SELECT [group_id] = @group_id, [user_id] = @user_id ) I 
		ON B.[group_id] = I.[group_id] AND B.[user_id] = I.[user_id]
	WHEN NOT MATCHED BY TARGET THEN INSERT
		( [group_id], [user_id])
	VALUES
		(@group_id, @user_id)
	;	
END
GO

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
	IF EXISTS (SELECT * FROM [dbo].[mTokens] WHERE [user_id] = @user_id)
		SELECT TOP 1
			U.[user_id]
			,U.[name]
			,U.[pass]
			,T.[token]
			,T.[token_secret]
			,A.[cons_key]
			,A.[cons_secret]
			,A.[id_app]
		FROM [dbo].[mAccounts] U
			JOIN [dbo].[mTokens] T ON T.[user_id] = U.[user_id]
			JOIN [dbo].[mApplications] A ON A.[id_app] = T.[id_app]
		WHERE 
			U.[user_id] = @user_id
	ELSE
		SELECT TOP 1
			U.[user_id]
			,U.[name]
			,U.[pass]
			,[token] = NULL
			,[token_secret] = NULL
			,A.[cons_key]
			,A.[cons_secret]
			,A.[id_app]
		FROM [dbo].[mAccounts] U
			LEFT JOIN (SELECT TOP 1 [user_id] = @user_id, A.[cons_key]
				,A.[cons_secret]
				,A.[id_app] 
				FROM [dbo].[mApplications] A) A ON A.[user_id] = U.[user_id]
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
	CREATE TABLE #tmp([acprID] BIGINT, [user_id] BIGINT, [ProxyID] BIGINT, 
		[ip] NVARCHAR(50), [port] INT, [prtypeID] TINYINT, [typename] NVARCHAR(50))

	INSERT INTO #tmp([acprID], [user_id], [ProxyID], [ip], [port], [prtypeID], [typename])
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
		AND ISNULL(P.[blocked], 0) <> 1
		AND PA.[acprID] IS NULL

	UPDATE P
			SET P.[blocked] = 1
	FROM [dbo].[mProxies] P
		INNER JOIN #tmp T ON T.[ProxyID] = P.[ProxyID]
	
	SELECT [acprID], [user_id], [ProxyID], [ip], [port], [prtypeID], [typename] FROM #tmp
END
GO

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- ================================================
-- Author:	Vetrov
-- Description:	Saving proxies 2 DB
-- ================================================
ALTER PROCEDURE [dbo].[spSaveProxy]
	@ip NVARCHAR(50)
	,@port INT 
	,@prtypeID INT 
	,@id_cn INT 
	,@alive INT 
AS BEGIN
	SET NOCOUNT ON;
		MERGE [dbo].[mProxies] P
		USING (SELECT [ip] = @ip, [port] = @port) I 
			ON P.[ip] = I.[ip] AND P.[port] = I.[port]
		WHEN NOT MATCHED BY TARGET THEN INSERT
			([ip], [port], [prtypeID], [id_cn], [alive])
		VALUES
			(@ip, @port, @prtypeID, @id_cn, @alive)
		;
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

