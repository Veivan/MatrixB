
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
CREATE TABLE [dbo].[mProxyAcc](
	[acprID] [bigint] IDENTITY(1,1) NOT NULL,
	[user_id] [bigint] NOT NULL,
	[ProxyID] [bigint] NOT NULL
) ON [PRIMARY]

