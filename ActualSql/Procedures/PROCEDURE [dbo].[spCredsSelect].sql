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
