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
