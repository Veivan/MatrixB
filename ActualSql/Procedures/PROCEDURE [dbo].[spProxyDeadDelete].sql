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
