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
	FROM [dbo].[mAccounts] A 
		INNER JOIN [dbo].[mBelong2] B ON B.[user_id] = A.[user_id] 
		LEFT JOIN [dbo].[mTokens] T ON T.[user_id] = A.[user_id]
		WHERE 	
			T.[id_creds] IS NOT NULL
			AND (@group_id IS NULL OR B.[group_id] = @group_id)
			AND (@enabled IS NULL OR  A.[enabled] = @enabled)
			--AND A.[enabled] IS NULL
	ORDER BY A.[user_id] 
END
GO
