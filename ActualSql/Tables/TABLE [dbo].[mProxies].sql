
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
CREATE TABLE [dbo].[mProxies](
	[ProxyID] [bigint] IDENTITY(1,1) NOT NULL,
	[ip] [nvarchar](50) COLLATE Cyrillic_General_CI_AS NOT NULL,
	[port] [int] NOT NULL,
	[prtypeID] [tinyint] NOT NULL,
	[id_cn] [int] NOT NULL,
	[alive] [bit] NOT NULL,
	[blocked] [bit] NULL,
	[tempblocked] [bigint] NULL
) ON [PRIMARY]

