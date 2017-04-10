SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- ================================================
-- Author:	Vetrov
-- Description:	Saving exec result 2 DB
-- ================================================
ALTER PROCEDURE [dbo].[spExecutionInsert]
	@user_id BIGINT 
	,@id_task BIGINT 
	,@act_id BIGINT 
	,@result BIT 
	,@failreason NVARCHAR(MAX)
	,@execdate BIGINT 
AS BEGIN
	DECLARE @fr_id INT = NULL
	SET NOCOUNT ON;

	IF (@result = 0) BEGIN
		IF (@failreason IS NULL OR LTRIM(RTRIM(@failreason)) = '')
			SELECT @fr_id = [fr_id]
			FROM [dbo].[DicFailReason]
			WHERE [errorcode] = 999
		ELSE 
			IF (@failreason LIKE '%Connect to%')
				SELECT @fr_id = [fr_id]
				FROM [dbo].[DicFailReason]
				WHERE [errorcode] = 1006
			ELSE 
				SELECT @fr_id = [fr_id]
				FROM [dbo].[DicFailReason]
				WHERE [failreason] LIKE @failreason

		IF (@fr_id IS NULL) BEGIN
			INSERT [dbo].[DicFailReason] ([failreason]) VALUES (@failreason)
			SELECT @fr_id = SCOPE_IDENTITY()
		END
	END
	
	INSERT INTO [dbo].[mExecution] ([user_id],[id_task],[act_id],[result],[fr_id],[execdate]) 
	VALUES (@user_id, @id_task, @act_id, @result, @fr_id, @execdate)
END
GO
