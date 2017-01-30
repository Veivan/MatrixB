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

	IF (@ProxyID = 0) BEGIN
		SELECT @ProxyID = [ProxyID] FROM [dbo].[mProxyAcc] WHERE [user_id] = @user_id
		UPDATE [dbo].[mProxies] SET [alive] = 0 WHERE [ProxyID] = @ProxyID
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
