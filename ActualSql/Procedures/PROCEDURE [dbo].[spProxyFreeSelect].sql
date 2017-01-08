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
