SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- ================================================
-- Author:	Vetrov
-- Description:	Selecting executed tasks info
-- ================================================
ALTER PROCEDURE [dbo].[spExecutionSelect]
	@user_id BIGINT,
	@tdate DATETIME 
AS BEGIN
	SET NOCOUNT ON;

	SELECT  
      [id_task]
      ,[result]     
	FROM [dbo].[mExecution]
	WHERE 
		[user_id] = @user_id
		AND
		DATEPART(dy, [dbo].[UnixTime2DateTime]([execdate])) = DATEPART(dy, @TDate)
	ORDER BY [id_Task]
END
GO
