SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:	Vetrov
-- Description:	Add new record of Timing List
-- =============================================
ALTER PROCEDURE [dbo].[spTimingRecordAdd]
	@tmng_id BIGINT
	,@TaskType NVARCHAR(50)
	,@tstamp BIGINT
AS BEGIN
	SET NOCOUNT ON

	DECLARE @id_TaskType SMALLINT

	SELECT @id_TaskType = [id_TaskType]
	FROM [dbo].[DicTaskType] WHERE UPPER([TypeMean]) = UPPER(@TaskType)

	INSERT INTO [dbo].[mTimingList] ([tmng_id], [id_TaskType], [tstamp])
	VALUES (@tmng_id, @id_TaskType, @tstamp)
END
GO
