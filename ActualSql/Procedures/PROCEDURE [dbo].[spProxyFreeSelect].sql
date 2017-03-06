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
			AND PA.[acprID] IS NULL) T ON T.[ProxyID] = P.[ProxyID]
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
		P.[tempblocked] = @user_id

END
GO
