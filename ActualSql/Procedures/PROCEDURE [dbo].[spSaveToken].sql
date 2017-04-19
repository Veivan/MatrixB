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
