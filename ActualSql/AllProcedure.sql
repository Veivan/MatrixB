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
	@screen_name NVARCHAR(150), --Login
	@email NVARCHAR(50),  
	@phone NVARCHAR(50),  
	@pass NVARCHAR(50),  
	@twitter_id BIGINT,
	@group_id INT,
	@mailpass NVARCHAR(50),
	@gender BIT
AS BEGIN
	SET NOCOUNT ON

	IF @name IS NULL SET @name = @screen_name
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
			   ,[gender]
			   ,[finsert])
		 VALUES
			   (@name, @screen_name, @email, @phone, @pass, @twitter_id, @mailpass, @gender, GETDATE())
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
			  ,[gender] = @gender
		 WHERE [user_id] = @user_id

	IF (@group_id <> -1)
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
-- Description:	Selecting Accounts
-- ================================================
ALTER PROCEDURE [dbo].[spAccountsSelect]
	@group_id INT = NULL
	,@enabled BIT = NULL
AS BEGIN
	SET NOCOUNT ON;

	/*CREATE TABLE #t ([user_id] INT)
	INSERT #t ([user_id]) VALUES (138)
	INSERT #t ([user_id]) VALUES (210)
	SELECT [user_id] FROM #t*/

	SELECT 
		A.[user_id]
		,[screen_name]
		,[pass]
		,[phone]
		,[email]
		,[mailpass] 
		,A.[enabled] 
		,[gender] = ISNULL(A.[gender], -1)
	FROM [dbo].[mAccounts] A 
		--INNER JOIN [dbo].[mBelong2] B ON B.[user_id] = A.[user_id] 
		LEFT JOIN [dbo].[mTokens] T ON T.[user_id] = A.[user_id]
	WHERE 	
		--T.[id_creds] IS NOT NULL
		--AND 		(@group_id IS NULL OR B.[group_id] = @group_id)
		--AND 
		(@enabled IS NULL OR  A.[enabled] = @enabled)
	ORDER BY A.[user_id] 
END
GO

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:	Vetrov
-- Description:	Update extended account props
-- =============================================
ALTER PROCEDURE [dbo].[spAccountUpdExt]
	@user_id BIGINT,
	@twitter_id BIGINT,
	@location NVARCHAR(50),  
	@followers_count INT,
	@friends_count INT,
	@listed_count INT,
	@statuses_count INT,
	@url NVARCHAR(500), 
	@description NVARCHAR(MAX),  
	@created_at datetimeoffset(2),
	@utc_offset INT,
	@time_zone NVARCHAR(50),  
	@lang NVARCHAR(50),  
	@geo_enabled BIT,
	@lasttweet_at datetimeoffset(2),
	@default_profile BIT,
	@default_profile_image BIT,
	@verified BIT
AS BEGIN
	SET NOCOUNT ON

	DECLARE @lang_id INT

	MERGE [dbo].[DicLang] D
	USING (SELECT [lang] = @lang ) I 
		ON D.[lang] = I.[lang]
	WHEN NOT MATCHED BY TARGET THEN INSERT
		([lang])
	VALUES
		(@lang)
	;

	SELECT @lang_id = [lang_id] FROM [dbo].[DicLang] WHERE [lang] = @lang

	MERGE [dbo].[mAccounts] A
	USING (SELECT [user_id] = @user_id ) I 
		ON A.[user_id] = I.[user_id]
	WHEN MATCHED THEN UPDATE SET
		[twitter_id] = @twitter_id
		,[location] = @location
		,[followers_count] = @followers_count
		,[friends_count] = @friends_count
		,[listed_count] = @listed_count
		,[statuses_count] = @statuses_count
		,[url] = @url
		,[description] = @description
		,[created_at] = @created_at
		,[utc_offset] = @utc_offset
		,[time_zone] = @time_zone
		,[lang_id] = @lang_id
		,[geo_enabled] = @geo_enabled
		,[lasttweet_at] = @lasttweet_at
		,[default_profile] = @default_profile
		,[default_profile_image] = @default_profile_image
		,[verified] = @verified
		,[sinsert] = GETDATE()
	;

END
GO

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:	Vetrov
-- Description:	Set account enabled or not
-- =============================================
ALTER PROCEDURE [dbo].[spAccSetAccessibility]
	@user_id BIGINT,
	@enabled BIT,
	@errorcode INT
AS BEGIN
	SET NOCOUNT ON
	DECLARE @adr_id INT = NULL

	IF (@enabled = 0) BEGIN
		SELECT @adr_id = [adr_id] 
		FROM [dbo].[DicAccDisReason]
		WHERE [errorcode] = @errorcode
	END
	UPDATE [dbo].[mAccounts] SET 
		[enabled] = @enabled
		,[adr_id] = @adr_id
	WHERE [user_id] = @user_id
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
			,U.[screen_name]
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
			,U.[screen_name]
			,U.[pass]
			,[token] = NULL
			,[token_secret] = NULL
			,A.[cons_key]
			,A.[cons_secret]
			,A.[id_app]
		FROM [dbo].[mAccounts] U
			INNER JOIN [dbo].[mAcc2App] P ON P.[user_id] = U.[user_id]
			INNER JOIN [dbo].[mApplications] A ON A.[id_app] = P.[id_app]
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
-- Description:	Saving exec result 2 DB
-- ================================================
ALTER PROCEDURE [dbo].[spExecutionInsert]
	@user_id BIGINT 
	,@id_task BIGINT 
	,@act_id BIGINT 
	,@result BIT 
	,@failreason NVARCHAR(MAX)
	,@execdate BIGINT 
AS BEGIN
	DECLARE @fr_id INT = NULL
	SET NOCOUNT ON;

	IF (@result = 0) BEGIN
		IF (@failreason IS NULL OR LTRIM(RTRIM(@failreason)) = '')
			SELECT @fr_id = [fr_id]
			FROM [dbo].[DicFailReason]
			WHERE [errorcode] = 999
		ELSE 
			IF (@failreason LIKE '%Connect to%')
				SELECT @fr_id = [fr_id]
				FROM [dbo].[DicFailReason]
				WHERE [errorcode] = 1006
			ELSE 
				SELECT @fr_id = [fr_id]
				FROM [dbo].[DicFailReason]
				WHERE [failreason] LIKE @failreason

		IF (@fr_id IS NULL) BEGIN
			INSERT [dbo].[DicFailReason] ([failreason]) VALUES (@failreason)
			SELECT @fr_id = SCOPE_IDENTITY()
		END
	END
	
	INSERT INTO [dbo].[mExecution] ([user_id],[id_task],[act_id],[result],[fr_id],[execdate]) 
	VALUES (@user_id, @id_task, @act_id, @result, @fr_id, @execdate)
END
GO

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- ================================================
-- Author:	Vetrov
-- Description:	Selecting executed tasks info
-- ================================================
ALTER PROCEDURE [dbo].[spExecutionSelect]
	@user_id BIGINT,
	@tdate DATETIME 
AS BEGIN
	SET NOCOUNT ON;

	SELECT  
      [id_task]
      ,[result]     
	FROM [dbo].[mExecution]
	WHERE 
		[user_id] = @user_id
		AND
		DATEPART(dy, [dbo].[UnixTime2DateTime]([execdate])) = DATEPART(dy, @TDate)
	ORDER BY [id_Task]
END
GO

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:	Vetrov
-- Description:	Fill workgroups randomly
-- =============================================
ALTER PROCEDURE [dbo].[spFillBelong2Rand]
AS BEGIN
	SET NOCOUNT ON
	DECLARE @top INT

	DELETE B
	FROM [dbo].[mBelong2] B
		INNER JOIN  [dbo].[DicGroups] G ON G.group_id = B.group_id
	WHERE G.forwork = 1
  
	DECLARE @accs TABLE(nnid INT IDENTITY(1,1), [user_id] INT )
	INSERT @accs ([user_id])
	SELECT A.[user_id] FROM dbo.mAccounts AS A WHERE A.[enabled] = 1
	ORDER BY NEWID()

	DECLARE @tbl TABLE(RowID INT IDENTITY(1, 1) PRIMARY KEY, [group_id] INT, [goal] INT) 
	DECLARE	@group_id INT, @goal INT, @count INT, @iRow INT  

	INSERT @tbl ([group_id], [goal])
	SELECT D.[group_id], [goal]
	FROM [dbo].[DicGroups] D
		INNER JOIN [dbo].[mGroupRegim] R ON R.[groupid] = D.[group_id]
	WHERE [forwork] = 1 AND ISNULL([goal], 0) > 0 
	ORDER BY [WakeHour]

	SET @count = @@ROWCOUNT 
	SET @iRow = 1 
	WHILE @iRow <= @count BEGIN 
		SELECT @group_id = [group_id], @goal = [goal] FROM @tbl WHERE RowID = @iRow 
		INSERT mBelong2 ([group_id], [user_id])
		SELECT TOP (@goal) @group_id, [user_id] FROM @accs  
		DELETE TOP (@goal) FROM @accs
		SET @iRow = @iRow + 1 
	END 
END
GO

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:	Vetrov
-- Description:	Update Follow info
--
-- @fwtype = 0 - follower, 1 - friend, null - unfollow
-- =============================================
ALTER PROCEDURE [dbo].[spFollowInfoUpd]
	@user_id BIGINT
	,@twitter_id BIGINT
	,@fwtype BIT
AS BEGIN
	SET NOCOUNT ON

	MERGE [dbo].[mFollowInfo] M
	USING (SELECT [user_id] = @user_id, [twitter_id] = @twitter_id) I 
		ON M.[user_id] = I.[user_id] AND M.[twitter_id] = I.[twitter_id]
	WHEN NOT MATCHED BY TARGET THEN 
		INSERT ([user_id], [twitter_id], [fwtype], [date_upd])
		VALUES (@user_id, @twitter_id, @fwtype, GETDATE())
	WHEN MATCHED THEN 
		UPDATE SET 
			[fwtype] = @fwtype,
			[date_upd] = GETDATE()
	;

END
GO

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:	Vetrov
-- Description:	Select picture from DB
-- @pic_id - id from mPicture
-- image ptype_id is 3 (PIC4TWIT)
-- =============================================
ALTER PROCEDURE [dbo].[spGetPictureByID]
	@pic_id INT
	,@pic VARBINARY(MAX) OUTPUT
AS
BEGIN
	SET NOCOUNT ON;	
 
	SELECT @pic = [fpicture] FROM [dbo].[mPicture] P
	WHERE [pic_id] = @pic_id

	--PRINT @pic_id
END
GO

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

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:	Vetrov
-- Description:	Add image from file
-- @gender = 0 (FEMALE) or = 1 (MALE) or = null (NEUTRAL)
-- @ptype_id is link to DicPicType
-- =============================================
ALTER PROCEDURE [dbo].[spLoadFile]
	@FileName nvarchar(100)
	,@gender BIT
	,@ptype_id INT
AS
BEGIN
	SET NOCOUNT ON;
	
	DECLARE @cmd nvarchar(4000) = 'INSERT INTO mPicture(gender, ptype_id, fpicture) SELECT ' 
		+ CAST(@gender AS NVARCHAR) + ',' + CAST(@ptype_id AS NVARCHAR)
		+ ', * FROM OPENROWSET(BULK ''' 
		+ @FileName + ''', SINGLE_BLOB) AS Picture'
	--PRINT @cmd
	EXEC (@cmd)
END
GO

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:	Vetrov
-- Description:	Add image from binary array
-- @gender = 0 (FEMALE) or = 1 (MALE) or = null (NEUTRAL)
-- @ptype_id is link to DicPicType
-- =============================================
ALTER PROCEDURE [dbo].[spLoadImage]
	@pic VARBINARY(MAX)
	,@gender BIT
	,@ptype_id INT
AS
BEGIN
	SET NOCOUNT ON;	
	INSERT INTO [dbo].[mPicture] (fpicture, gender, ptype_id) VALUES (@pic, @gender, @ptype_id)
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

	IF (@ProxyID = 0) BEGIN -- Acc has bad proxy - make proxy dead
		SELECT @ProxyID = [ProxyID] FROM [dbo].[mProxyAcc] WHERE [user_id] = @user_id
		UPDATE [dbo].[mProxies] SET [alive] = 0 WHERE [ProxyID] = @ProxyID
		DELETE [dbo].[mProxyAcc] WHERE [user_id] = @user_id
	END
	ELSE
	IF (@ProxyID = -1) BEGIN -- Make proxy free
		DELETE [dbo].[mProxyAcc] WHERE [user_id] = @user_id
	END
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
ALTER PROCEDURE [dbo].[spProxyDeadDelete]
AS BEGIN
	SET NOCOUNT ON;

	DELETE PA
	FROM [dbo].[mProxyAcc] PA
		INNER JOIN [dbo].[mProxies] P ON P.[ProxyID] = PA.[ProxyID]
	WHERE 
		P.[alive] = 0

	DELETE [dbo].[mProxies]
	WHERE 
		[alive] = 0
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
	@user_id INT
AS BEGIN
	SET NOCOUNT ON;

	-- Резервирование прокси
	UPDATE P SET 
		P.[blocked] = 1, 
		P.[tempblocked] = @user_id
	FROM [dbo].[mProxies] P
		INNER JOIN 
		(
			SELECT TOP 3
				P.[ProxyID]
			FROM [dbo].[mProxies] P
				LEFT JOIN [dbo].[mProxyAcc] PA ON P.[ProxyID] = PA.[ProxyID]
				JOIN [dbo].[DicProxyType] D ON P.[prtypeID] = D.[prtypeID]
			WHERE 
				P.[alive] = 1
				AND ISNULL(P.[blocked], 0) <> 1
				AND PA.[acprID] IS NULL
		) T ON T.[ProxyID] = P.[ProxyID]
	-- Выбор зарезервированных прокси
	SELECT 
		P.[ProxyID]
		,P.[ip]
		,P.[port]
		,P.[prtypeID]
		,[typename] = LTRIM(RTRIM(D.[typename]))
	FROM [dbo].[mProxies] P
		JOIN [dbo].[DicProxyType] D ON P.[prtypeID] = D.[prtypeID]
	WHERE 
		P.[blocked] = 1
		AND
		P.[tempblocked] = @user_id

END
GO

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- ================================================
-- Author:	Vetrov
-- Description:	Store random texts in DB
-- ================================================
ALTER PROCEDURE [dbo].[spRandTextAdd]
	@text NVARCHAR(MAX)
AS BEGIN
	SET NOCOUNT ON;

	MERGE [dbo].[mRandText] T
	USING (SELECT [randtext] = @text ) I 
		ON T.[randtext] = I.[randtext]
	WHEN NOT MATCHED BY TARGET THEN INSERT
		([randtext])
	VALUES
		(@text)
	;
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
	DECLARE @ProxyID BIGINT = NULL
	SET NOCOUNT ON;
	IF (@alive = 0) BEGIN
		SELECT @ProxyID = [ProxyID] FROM [dbo].[mProxies]
		WHERE [ip] = @ip AND [port] = @port
		IF (@ProxyID IS NOT NULL) BEGIN
			DELETE [dbo].[mProxyAcc] WHERE [ProxyID] = @ProxyID
			DELETE [dbo].[mProxies] WHERE [ProxyID] = @ProxyID
		END
	END
	ELSE

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
-- Description:	Saving tokens 2 DB
-- ================================================
ALTER PROCEDURE [dbo].[spSaveToken]
	@user_id BIGINT 
	,@id_app BIGINT 
	,@token NVARCHAR(50) 
	,@token_secret NVARCHAR(50) 
AS BEGIN
	SET NOCOUNT ON;

	MERGE [dbo].[mTokens] P
	USING (SELECT [user_id] = @user_id, [id_app] = @id_app) I 
		ON P.[user_id] = I.[user_id] AND P.[id_app] = I.[id_app]
	WHEN NOT MATCHED BY TARGET THEN INSERT
		([user_id], [id_app], [token], [token_secret])
	VALUES
		(@user_id, @id_app, @token, @token_secret)
	WHEN MATCHED THEN UPDATE SET
		[token] = @token
		,[token_secret] = @token_secret
	;
END
GO

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- ================================================
-- Author:	Vetrov
-- Description:	Store Status in DB
-- ================================================
ALTER PROCEDURE [dbo].[spStatusAdd]
	@tw_id BIGINT
	,@status NVARCHAR(1000)
	,@creator_id BIGINT
	,@created_at datetimeoffset(2)
	,@favorite_count INT
	,@in_reply_to_screen_name NVARCHAR(50)
	,@in_reply_to_status_id BIGINT
	,@in_reply_to_user_id BIGINT
	,@lang NVARCHAR(50)
	,@retweet_count INT
	,@text NVARCHAR(500)
	,@place_json NVARCHAR(500)
	,@coordinates_json NVARCHAR(500)
	,@favorited BIT
	,@retweeted BIT
	,@isRetweet BIT
	,@retweeted_id BIGINT
AS BEGIN
	SET NOCOUNT ON;

	DECLARE @lang_id INT

	MERGE [dbo].[DicLang] D
	USING (SELECT [lang] = @lang ) I 
		ON D.[lang] = I.[lang]
	WHEN NOT MATCHED BY TARGET THEN INSERT
		([lang])
	VALUES
		(@lang)
	;

	SELECT @lang_id = [lang_id] FROM [dbo].[DicLang] WHERE [lang] = @lang

	MERGE [dbo].[twTwits] T
	USING (SELECT [tw_id] = @tw_id ) I 
		ON T.[tw_id] = I.[tw_id]
	WHEN NOT MATCHED BY TARGET THEN INSERT
		([tw_id],[status], [creator_id], [created_at], [favorite_count], 
		[in_reply_to_screen_name], [in_reply_to_status_id], [in_reply_to_user_id], [lang_id], [retweet_count], 
		[text], [place_json], [coordinates_json], [favorited], [retweeted], [isRetweet], [retweeted_id])
	VALUES
		(@tw_id, @status, @creator_id, @created_at, @favorite_count, 
		@in_reply_to_screen_name, @in_reply_to_status_id, @in_reply_to_user_id, @lang_id, @retweet_count,
		@text, @place_json, @coordinates_json, @favorited, @retweeted, @isRetweet, @retweeted_id)
	WHEN MATCHED THEN UPDATE SET
		[status] = @status
		,[favorite_count] = @favorite_count
		,[retweet_count] = @retweet_count
		,[favorited] = @favorited
		,[retweeted] = @retweeted
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
		,[group_id] = ISNULL(T.[group_id], 0)
		,D.[TypeMean]
	FROM [dbo].[mTasks] T
		LEFT JOIN [dbo].[DicTaskType] D ON D.[id_TaskType] = T.[id_TaskType]
	WHERE 
		T.[TaskDate] = @TDate
		OR
		T.[IsRepeat] = 1
	ORDER BY T.[ordernum]
END
GO

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:	Vetrov
-- Description:	Add new Timing record
-- =============================================
ALTER PROCEDURE [dbo].[spTimingAdd]
	@user_id BIGINT,
	@tmng_id BIGINT OUTPUT
AS BEGIN
	SET NOCOUNT ON

	INSERT INTO [dbo].[mTimings] ([user_id], [created_at])
	VALUES (@user_id, GETDATE())
	SET @tmng_id = @@IDENTITY
END
GO

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:	Vetrov
-- Description:	Add new record of Timing List
-- =============================================
ALTER PROCEDURE [dbo].[spTimingRecordAdd]
	@tmng_id BIGINT
	,@TaskType NVARCHAR(50)
	,@tstamp BIGINT
AS BEGIN
	SET NOCOUNT ON

	DECLARE @id_TaskType SMALLINT

	SELECT @id_TaskType = [id_TaskType]
	FROM [dbo].[DicTaskType] WHERE UPPER([TypeMean]) = UPPER(@TaskType)

	INSERT INTO [dbo].[mTimingList] ([tmng_id], [id_TaskType], [tstamp])
	VALUES (@tmng_id, @id_TaskType, @tstamp)
END
GO

