SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:	Vetrov
-- Description:	Update Follow info
--
-- @fwtype = 0 - follower, 1 - friend, null - unfollow
-- =============================================
ALTER PROCEDURE [dbo].[spFollowInfoUpd]
	@user_id BIGINT
	,@twitter_id BIGINT
	,@fwtype BIT
AS BEGIN
	SET NOCOUNT ON

	MERGE [dbo].[mFollowInfo] M
	USING (SELECT [user_id] = @user_id, [twitter_id] = @twitter_id) I 
		ON M.[user_id] = I.[user_id] AND M.[twitter_id] = I.[twitter_id]
	WHEN NOT MATCHED BY TARGET THEN 
		INSERT ([user_id], [twitter_id], [fwtype], [date_upd])
		VALUES (@user_id, @twitter_id, @fwtype, GETDATE())
	WHEN MATCHED THEN 
		UPDATE SET 
			[fwtype] = @fwtype,
			[date_upd] = GETDATE()
	;

END
GO
