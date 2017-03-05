USE MatrixB
SELECT 
	[email]
	,[pass]
	,[screen_name]
	,A.[user_id]

	/*[screen_name]
	,[pass]
	,[phone]
	,[email]
	,[mailpass] */
FROM [dbo].[mAccounts] A 
	INNER JOIN [dbo].[mBelong2] B ON B.user_id = A.user_id AND B.group_id = 3
	LEFT JOIN [dbo].[mTokens] T ON T.user_id = A.user_id
	WHERE 	
		T.id_creds IS NOT NULL
		AND A.[enabled] IS NULL
		--AND A.[url] = '1'

/*
update [dbo].[mAccounts]  set [enabled] = 1 where user_id in (58, 61,62)
update [dbo].[mAccounts]  set [enabled] = 1 where user_id = 12

UPDATE A
SET A.[enabled] = A.[geo_enabled]
FROM [dbo].[mAccounts] A 
	INNER JOIN [dbo].[mBelong2] B ON B.user_id = A.user_id AND B.group_id = 1
	LEFT JOIN [dbo].[mTokens] T ON T.user_id = A.user_id
	WHERE 
		T.id_creds IS NOT NULL

SELECT 
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

SELECT E.*
	  ,[date] = dateadd(S, [execdate], '1970-01-01')

  FROM [MatrixB].[dbo].[mExecution] E
  order by ae_id


*/