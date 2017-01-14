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
