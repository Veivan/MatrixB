/****** Script for SelectTopNRows command from SSMS  ******/
SELECT  1
      ,[created_at]
 ,DATEDIFF(SECOND,{d '1970-01-01'}, [created_at])
 , DATEPART(D, [created_at])
 , DATEPART(HH, [created_at])
   FROM [MatrixB].[dbo].[twTwits]
   order by [created_at] 

 -- truncate table [MatrixB].[dbo].[twTwits]

 SELECT COUNT (*), DATEPART(D, [created_at])
 , DATEPART(HH, [created_at])

  FROM [MatrixB].[dbo].[twTwits]

GROUP BY  DATEPART(D, [created_at])
 , DATEPART(HH, [created_at])

ORDER BY  DATEPART(D, [created_at])
 , DATEPART(HH, [created_at])

