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
