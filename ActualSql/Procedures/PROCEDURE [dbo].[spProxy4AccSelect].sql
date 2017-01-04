SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- ================================================
-- Author:	Vetrov
-- Description:	Selecting proxy 4 account
-- ================================================
ALTER PROCEDURE [dbo].[spProxy4AccSelect]
	@user_id int
AS BEGIN
	SET NOCOUNT ON;

	SELECT TOP 1
		PA.[acprID]
		,PA.[user_id]
		,PA.[ProxyID]
		,P.[ip]
		,P.[port]
		,P.[prtypeID]
		,[typename] = LTRIM(RTRIM(D.[typename]))
	FROM [dbo].[mProxyAcc] PA
		JOIN [dbo].[mProxies] P ON P.[ProxyID] = PA.[ProxyID]
		JOIN [dbo].[DicProxyType] D ON P.[prtypeID] = D.[prtypeID]
	WHERE 
		PA.[user_id] = @user_id
		AND P.[alive] = 1
END
GO
