SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:	Vetrov
-- Description:	Set account enabled or not
-- =============================================
ALTER PROCEDURE [dbo].[spAccSetAccessibility]
	@user_id BIGINT,
	@enabled BIT,
	@errorcode INT
AS BEGIN
	SET NOCOUNT ON
	DECLARE @adr_id INT = NULL

	IF (@enabled = 0) BEGIN
		SELECT @adr_id = [adr_id] 
		FROM [dbo].[DicAccDisReason]
		WHERE [errorcode] = @errorcode
	END
	UPDATE [dbo].[mAccounts] SET 
		[enabled] = @enabled
		,[adr_id] = @adr_id
	WHERE [user_id] = @user_id
END
GO
