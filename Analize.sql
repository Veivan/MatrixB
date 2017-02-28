  select A.user_id, A.screen_name from mAccounts A
  join mExecution E on E.user_id = A.user_id 
	and dateadd(S, [execdate], '1970-01-01') > '2017-02-24'
	and E.result = 0
	and id_task = 14
  group by A.user_id, A.screen_name

 
SELECT *
	  , [date] = dateadd(S, [execdate], '1970-01-01') 
 FROM [MatrixB].[dbo].[mExecution] E
	JOIN [dbo].[mTasks] T ON T.id_Task = E.id_task
WHERE dateadd(S, [execdate], '1970-01-01') > '2017-02-28'
	and TContent = '#helpchildren'