USE MatrixB
SELECT 
	[email]
	,[pass]
	,[screen_name]
	,A.[user_id]
	
	--, B.group_id
	, A.[enabled]

	/*[screen_name]
	,[pass]
	,[phone]
	,[email]
	,[mailpass] */
FROM [dbo].[mAccounts] A 
	--INNER JOIN [dbo].[mBelong2] B ON B.user_id = A.user_id --AND B.group_id = 4
	LEFT JOIN [dbo].[mTokens] T ON T.user_id = A.user_id
WHERE 	
	T.id_creds IS  NULL
	--AND A.[enabled] IS NULL
	--AND A.[enabled] = 1
ORDER BY user_id DESC


/*

UPDATE A
SET A.[enabled] = A.[geo_enabled]
FROM [dbo].[mAccounts] A 
	INNER JOIN [dbo].[mBelong2] B ON B.user_id = A.user_id AND B.group_id = 1
	LEFT JOIN [dbo].[mTokens] T ON T.user_id = A.user_id
	WHERE 
		T.id_creds IS NOT NULL

-- Сколько прокси свободно?
	SELECT *
	FROM [dbo].[mProxies] P
		LEFT JOIN [dbo].[mProxyAcc] PA ON P.[ProxyID] = PA.[ProxyID]
		JOIN [dbo].[DicProxyType] D ON P.[prtypeID] = D.[prtypeID]
	WHERE 
		P.[alive] = 1
		AND ISNULL(P.[blocked], 0) <> 1
		AND PA.[acprID] IS NULL

	-- Выбор прокси, занятых акками без токенов
	DELETE FROM [dbo].[mProxyAcc]
	WHERE acprID IN (
		SELECT acprID
		FROM [dbo].[mProxyAcc] P
			LEFT JOIN [dbo].[mTokens] T ON T.user_id = P.user_id
		WHERE T.id_creds IS NULL 
	)

	-- Выбор прокси, занятых акками not enabled
	DELETE FROM [dbo].[mProxyAcc]
	WHERE acprID IN (
		SELECT acprID
		FROM [dbo].[mProxyAcc] P
			JOIN [dbo].[mAccounts] T ON T.user_id = P.user_id
		WHERE T.enabled = 0 
	)

SELECT E.*
	  ,[date] = dateadd(S, [execdate], '1970-01-01')
FROM [MatrixB].[dbo].[mExecution] E
WHERE DATEPART(dy, dateadd(S, [execdate], '1970-01-01')) = DATEPART(dy, GETDATE())
order by ae_id DESC


*/

