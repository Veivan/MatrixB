DECLARE @dt DATETIME
SET @dt = '2017-04-20'
--SET @dt = GETDATE()
SELECT E.*
	  ,[date] = dateadd(S, [execdate], '1970-01-01')
	  ,R.comment
	  ,P.id_app
FROM [MatrixB].[dbo].[mExecution] E
	LEFT JOIN [dbo].[DicFailReason] R ON R.fr_id = E.fr_id
	LEFT JOIN [dbo].[mAcc2App] P ON P.user_id = E.user_id
WHERE 
	/**/
	DATEPART(dy, dateadd(S, [execdate], '1970-01-01')) = DATEPART(dy, @dt)
	AND result = 1
	--AND E.fr_id <> 16 
	AND E.id_task = 39
	--AND id_app = 3
--E.user_id = 1753
order by ae_id DESC
return
SELECT DISTINCT E.user_id, A.enabled
FROM [MatrixB].[dbo].[mExecution] E
	LEFT JOIN [dbo].[mAccounts] A ON A.user_id = E.user_id
WHERE	
	DATEPART(dy, dateadd(S, [execdate], '1970-01-01')) = DATEPART(dy, '2017-04-20')
	AND E.fr_id = 6 

--GROUP by user_id 
--order by user_id 

return
 -- INSERT [mUnlock] ([ae_id], [dunlock])  VALUES (38211, GETDATE())

-- Сколько действий выполнили юзеры
SELECT [user_id], cn = COUNT ([user_id])
FROM [MatrixB].[dbo].[mExecution] E
WHERE DATEPART(dy, dateadd(S, [execdate], '1970-01-01')) = DATEPART(dy, @dt)
GROUP BY [user_id]
ORDER BY COUNT ([user_id]) DESC

-- Сколько токенов в приложении
SELECT TOP 1000 [bt_id]
      ,P.[id_app]
      ,P.[user_id]
	  ,A.enabled
	  ,T.id_app
  FROM [MatrixB].[dbo].[mAcc2App] P
	JOIN [dbo].[mAccounts] A ON A.user_id = P.user_id
	LEFT JOIN [dbo].[mTokens] T ON T.user_id = P.user_id
  where P.id_app = 7
  ORDER by [bt_id]

USE MatrixB
SELECT 
	[email]
	,[pass]
	,[screen_name]
	,A.[user_id]
	, B.group_id
	, A.[enabled]
	--, M.bt_id
	  ,T.id_app
FROM [dbo].[mAccounts] A 
	INNER JOIN [dbo].[mBelong2] B ON B.user_id = A.user_id AND B.group_id = 7
	LEFT JOIN [dbo].[mTokens] T ON T.user_id = A.user_id
	--LEFT JOIN [dbo].[mAcc2App] M ON M.user_id = A.user_id
WHERE --M.bt_id IS NULL
	T.id_creds IS NOT  NULL
	-- A.[enabled] IS NULL
	AND A.[enabled] = 1
ORDER BY user_id DESC

/************************************************************/
/************************************************************/
RETURN



UPDATE A
--SET A.[adr_id] = 1005,  A.[enabled] = 0
SET A.[enabled] = 1
FROM [dbo].[mAccounts] A 
	INNER JOIN [dbo].[mBelong2] B ON B.user_id = A.user_id AND B.group_id = 6
	--LEFT JOIN [dbo].[mTokens] T ON T.user_id = A.user_id
	WHERE 
		A.[enabled] IS NULL

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
	--DELETE 
	SELECT * 
	FROM [dbo].[mProxyAcc]
	WHERE acprID IN (
		SELECT acprID
		FROM [dbo].[mProxyAcc] P
			LEFT JOIN [dbo].[mTokens] T ON T.user_id = P.user_id
		WHERE T.id_creds IS NULL 
	)

	-- Выбор прокси, занятых акками not enabled
	--DELETE 
	SELECT * 
	FROM [dbo].[mProxyAcc]
	WHERE acprID IN (
		SELECT acprID
		FROM [dbo].[mProxyAcc] P
			JOIN [dbo].[mAccounts] T ON T.user_id = P.user_id
		WHERE T.enabled = 0 
	)

	-- Есть ссылки на дохлые прокси?
	SELECT *
	FROM [dbo].[mProxyAcc] PA
		LEFT JOIN [dbo].[mProxies] P ON P.[ProxyID] = PA.[ProxyID]
	WHERE 
		P.[alive] = 0



