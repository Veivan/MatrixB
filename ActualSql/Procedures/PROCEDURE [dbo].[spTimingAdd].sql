SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:	Vetrov
-- Description:	Add new Timing record
-- =============================================
ALTER PROCEDURE [dbo].[spTimingAdd]
	@user_id BIGINT,
	@tmng_id BIGINT OUTPUT
AS BEGIN
	SET NOCOUNT ON

	INSERT INTO [dbo].[mTimings] ([user_id], [created_at])
	VALUES (@user_id, GETDATE())
	SET @tmng_id = @@IDENTITY
END
GO
