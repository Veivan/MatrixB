 -- Какие юзеры не смогли отправить твит с текстом #helpchildren
 select A.user_id, A.screen_name from mAccounts A
  join mExecution E on E.user_id = A.user_id 
	and DATEPART(dy, dateadd(S, [execdate], '1970-01-01')) = DATEPART(dy, '2017-03-10')
	and E.result = 0
	and id_task IN ( 14, 19)
  group by A.user_id, A.screen_name

 
-- Сколько раз был выполнен твит с текстом TContent
SELECT *
	  , [date] = dateadd(S, [execdate], '1970-01-01') 
 FROM [MatrixB].[dbo].[mExecution] E
	JOIN [dbo].[mTasks] T ON T.id_Task = E.id_task
WHERE 
	DATEPART(dy, dateadd(S, [execdate], '1970-01-01')) = DATEPART(dy, '2017-03-10')
	and TContent LIKE '%#helpchildren%'
	AND E.result = 1
order by ae_id DESC

--Распределение твита по часам
select COUNT (*), h
FROM(
	SELECT  [tw_id]	
		,unixt = DATEDIFF(second,{d '1970-01-01'},[created_at])
		, h = DATEPART(HH, [created_at])
	FROM [MatrixB].[dbo].[twTwits]
	where DATEPART(dy, [created_at]) = DATEPART(dy, '2017-09-02')
) A 
GROUP BY h