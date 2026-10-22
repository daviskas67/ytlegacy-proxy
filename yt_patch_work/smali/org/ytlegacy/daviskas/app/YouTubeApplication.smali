.class public Lorg/ytlegacy/daviskas/app/YouTubeApplication;
.super Lorg/ytlegacy/daviskas/core/BaseApplication;
.source "SourceFile"

# interfaces
.implements Landroid/content/SharedPreferences$OnSharedPreferenceChangeListener;
.implements Lorg/ytlegacy/daviskas/core/async/bu;
.implements Lorg/ytlegacy/daviskas/core/client/bc;
.implements Lorg/ytlegacy/daviskas/core/client/be;
.implements Lorg/ytlegacy/daviskas/core/client/bl;
.implements Lorg/ytlegacy/daviskas/core/player/bg;


# instance fields
.field private A:Ljava/util/concurrent/atomic/AtomicReference;

.field private B:Ljava/util/concurrent/atomic/AtomicReference;

.field private C:I

.field private D:Lorg/ytlegacy/daviskas/app/a;

.field private a:Lorg/ytlegacy/daviskas/app/s;

.field private b:Landroid/provider/SearchRecentSuggestions;

.field private c:Lorg/ytlegacy/daviskas/core/suggest/a;

.field private d:Lorg/ytlegacy/daviskas/core/async/ck;

.field private e:Lorg/ytlegacy/daviskas/core/async/UserDelegator;

.field private f:Lorg/ytlegacy/daviskas/core/client/t;

.field private g:Lorg/ytlegacy/daviskas/core/client/al;

.field private h:Lorg/ytlegacy/daviskas/core/client/j;

.field private i:Lorg/ytlegacy/daviskas/core/client/am;

.field private j:Lorg/ytlegacy/daviskas/core/client/ai;

.field private k:Lorg/ytlegacy/daviskas/core/client/ag;

.field private l:Lorg/ytlegacy/daviskas/app/remote/bn;

.field private m:Lorg/ytlegacy/daviskas/app/remote/ca;

.field private n:Lorg/ytlegacy/daviskas/core/client/aq;

.field private o:Lorg/ytlegacy/daviskas/app/remote/cb;

.field private p:Lorg/ytlegacy/daviskas/app/remote/e;

.field private q:Lorg/ytlegacy/daviskas/app/remote/AtHomeConnection;

.field private r:Lorg/ytlegacy/daviskas/datalib/v3/ApiRequests;

.field private s:Lorg/ytlegacy/daviskas/core/client/f;

.field private t:Lorg/ytlegacy/daviskas/core/client/bm;

.field private u:Lorg/ytlegacy/daviskas/core/client/aw;

.field private v:Lorg/ytlegacy/daviskas/core/player/StatsTracker;

.field private w:Lorg/ytlegacy/daviskas/app/prefetch/e;

.field private x:Lorg/ytlegacy/daviskas/app/a/a;

.field private y:Lorg/ytlegacy/daviskas/app/remote/ae;

.field private z:Ljava/util/concurrent/atomic/AtomicReference;


# direct methods
.method public constructor <init>()V
    .locals 0

    .prologue
    .line 107
    invoke-direct {p0}, Lorg/ytlegacy/daviskas/core/BaseApplication;-><init>()V

    return-void
.end method

.method private Z()Z
    .locals 2

    .prologue
    .line 877
    invoke-static {p0}, Lorg/ytlegacy/daviskas/core/utils/s;->e(Landroid/content/Context;)I

    move-result v0

    const/16 v1, 0xce4

    if-lt v0, v1, :cond_0

    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->a:Lorg/ytlegacy/daviskas/app/s;

    iget-object v0, v0, Lorg/ytlegacy/daviskas/app/s;->a:Lorg/ytlegacy/daviskas/app/t;

    :cond_0
    const/4 v0, 0x0

    return v0
.end method


# virtual methods
.method public final A()Lorg/ytlegacy/daviskas/core/client/aq;
    .locals 1

    .prologue
    .line 681
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->n:Lorg/ytlegacy/daviskas/core/client/aq;

    return-object v0
.end method

.method public final B()Lorg/ytlegacy/daviskas/app/remote/bn;
    .locals 1

    .prologue
    .line 685
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->l:Lorg/ytlegacy/daviskas/app/remote/bn;

    return-object v0
.end method

.method public final C()Lorg/ytlegacy/daviskas/app/remote/cb;
    .locals 1

    .prologue
    .line 689
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->o:Lorg/ytlegacy/daviskas/app/remote/cb;

    return-object v0
.end method

.method public final D()Lorg/ytlegacy/daviskas/app/remote/ca;
    .locals 1

    .prologue
    .line 693
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->m:Lorg/ytlegacy/daviskas/app/remote/ca;

    return-object v0
.end method

.method public final E()Lorg/ytlegacy/daviskas/app/remote/e;
    .locals 1

    .prologue
    .line 697
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->p:Lorg/ytlegacy/daviskas/app/remote/e;

    return-object v0
.end method

.method public final F()Lorg/ytlegacy/daviskas/app/remote/AtHomeConnection;
    .locals 1

    .prologue
    .line 701
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->q:Lorg/ytlegacy/daviskas/app/remote/AtHomeConnection;

    return-object v0
.end method

.method public final G()Lorg/ytlegacy/daviskas/app/prefetch/e;
    .locals 1

    .prologue
    .line 718
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->w:Lorg/ytlegacy/daviskas/app/prefetch/e;

    return-object v0
.end method

.method public final H()Ljava/lang/String;
    .locals 4

    .prologue
    .line 731
    sget-object v2, Lorg/ytlegacy/daviskas/core/async/GDataRequestFactory;->a:Ljava/util/Set;

    .line 732
    invoke-virtual {p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->S()Landroid/content/SharedPreferences;

    move-result-object v0

    const-string v1, "country"

    const-string v3, ""

    invoke-interface {v0, v1, v3}, Landroid/content/SharedPreferences;->getString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    .line 733
    invoke-static {v0}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v1

    if-nez v1, :cond_1

    .line 734
    invoke-static {v0}, Lorg/ytlegacy/daviskas/core/utils/Util;->g(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    .line 735
    invoke-interface {v2, v0}, Ljava/util/Set;->contains(Ljava/lang/Object;)Z

    move-result v1

    if-eqz v1, :cond_1

    .line 771
    :cond_0
    :goto_0
    return-object v0

    :cond_1
    move-object v1, v0

    .line 740
    const-string v0, "phone"

    invoke-virtual {p0, v0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->getSystemService(Ljava/lang/String;)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Landroid/telephony/TelephonyManager;

    .line 742
    if-eqz v0, :cond_2

    .line 743
    invoke-virtual {v0}, Landroid/telephony/TelephonyManager;->getSimCountryIso()Ljava/lang/String;

    move-result-object v1

    .line 746
    invoke-static {v1}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v0

    if-nez v0, :cond_2

    .line 747
    invoke-static {v1}, Lorg/ytlegacy/daviskas/core/utils/Util;->g(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v1

    .line 748
    invoke-interface {v2, v1}, Ljava/util/Set;->contains(Ljava/lang/Object;)Z

    move-result v0

    if-eqz v0, :cond_2

    move-object v0, v1

    .line 749
    goto :goto_0

    .line 755
    :cond_2
    invoke-static {v1}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v0

    if-eqz v0, :cond_3

    .line 756
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->a:Lorg/ytlegacy/daviskas/app/s;

    invoke-virtual {v0}, Lorg/ytlegacy/daviskas/app/s;->N()Ljava/lang/String;

    move-result-object v0

    .line 757
    invoke-static {v0}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v1

    if-nez v1, :cond_3

    .line 758
    invoke-static {v0}, Lorg/ytlegacy/daviskas/core/utils/Util;->g(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    .line 759
    invoke-interface {v2, v0}, Ljava/util/Set;->contains(Ljava/lang/Object;)Z

    move-result v1

    if-nez v1, :cond_0

    .line 766
    :cond_3
    invoke-static {}, Ljava/util/Locale;->getDefault()Ljava/util/Locale;

    move-result-object v0

    invoke-virtual {v0}, Ljava/util/Locale;->getCountry()Ljava/lang/String;

    move-result-object v0

    invoke-static {v0}, Lorg/ytlegacy/daviskas/core/utils/Util;->g(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    .line 767
    invoke-interface {v2, v0}, Ljava/util/Set;->contains(Ljava/lang/Object;)Z

    move-result v1

    if-nez v1, :cond_0

    .line 771
    const/4 v0, 0x0

    goto :goto_0
.end method

.method public final I()Ljava/lang/String;
    .locals 1

    .prologue
    .line 839
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->B:Ljava/util/concurrent/atomic/AtomicReference;

    invoke-virtual {v0}, Ljava/util/concurrent/atomic/AtomicReference;->get()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Ljava/lang/String;

    return-object v0
.end method

.method public final J()I
    .locals 1

    .prologue
    .line 843
    iget v0, p0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->C:I

    return v0
.end method

.method public final K()Lorg/ytlegacy/daviskas/app/remote/ae;
    .locals 1

    .prologue
    .line 881
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->y:Lorg/ytlegacy/daviskas/app/remote/ae;

    return-object v0
.end method

.method public final a(I)V
    .locals 4

    .prologue
    const/4 v3, 0x2

    .line 817
    iput p1, p0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->C:I

    .line 819
    const/4 v0, -0x2

    if-ne p1, v0, :cond_0

    .line 820
    const-string v0, "Signed out"

    .line 830
    :goto_0
    invoke-virtual {p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->h()Lorg/ytlegacy/daviskas/core/Analytics;

    move-result-object v1

    const-string v2, "Engagement"

    invoke-interface {v1, v3, v2, v0, v3}, Lorg/ytlegacy/daviskas/core/Analytics;->a(ILjava/lang/String;Ljava/lang/String;I)V

    .line 832
    return-void

    .line 821
    :cond_0
    const/4 v0, -0x1

    if-ne p1, v0, :cond_1

    .line 822
    const-string v0, "Signed in"

    goto :goto_0

    .line 823
    :cond_1
    if-nez p1, :cond_2

    .line 824
    const-string v0, "No subscriptions"

    goto :goto_0

    .line 825
    :cond_2
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->a:Lorg/ytlegacy/daviskas/app/s;

    invoke-virtual {v0}, Lorg/ytlegacy/daviskas/app/s;->m()Z

    move-result v0

    if-eqz v0, :cond_3

    invoke-static {p0}, Lorg/ytlegacy/daviskas/app/prefetch/PrefetchService;->a(Landroid/content/Context;)Z

    move-result v0

    if-nez v0, :cond_4

    .line 826
    :cond_3
    const-string v0, "Subscriptions"

    goto :goto_0

    .line 828
    :cond_4
    const-string v0, "Prefetching"

    goto :goto_0
.end method

.method public final a(Lorg/ytlegacy/daviskas/core/model/UserAuth;)V
    .locals 2

    .prologue
    .line 776
    invoke-static {}, Lorg/ytlegacy/daviskas/core/L;->b()V

    .line 777
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->u:Lorg/ytlegacy/daviskas/core/client/aw;

    invoke-virtual {v0, p1}, Lorg/ytlegacy/daviskas/core/client/aw;->a(Lorg/ytlegacy/daviskas/core/model/UserAuth;)V

    .line 778
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->z:Ljava/util/concurrent/atomic/AtomicReference;

    iget-object v1, p1, Lorg/ytlegacy/daviskas/core/model/UserAuth;->username:Ljava/lang/String;

    invoke-virtual {v0, v1}, Ljava/util/concurrent/atomic/AtomicReference;->set(Ljava/lang/Object;)V

    .line 779
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->A:Ljava/util/concurrent/atomic/AtomicReference;

    iget-object v1, p1, Lorg/ytlegacy/daviskas/core/model/UserAuth;->channelId:Ljava/lang/String;

    invoke-virtual {v0, v1}, Ljava/util/concurrent/atomic/AtomicReference;->set(Ljava/lang/Object;)V

    .line 780
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->B:Ljava/util/concurrent/atomic/AtomicReference;

    iget-object v1, p1, Lorg/ytlegacy/daviskas/core/model/UserAuth;->account:Ljava/lang/String;

    invoke-virtual {v0, v1}, Ljava/util/concurrent/atomic/AtomicReference;->set(Ljava/lang/Object;)V

    .line 781
    const/4 v0, -0x1

    invoke-virtual {p0, v0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->a(I)V

    .line 782
    invoke-direct {p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->Z()Z

    move-result v0

    if-eqz v0, :cond_0

    .line 783
    invoke-static {p0}, Lorg/ytlegacy/daviskas/app/autosync/a;->a(Lorg/ytlegacy/daviskas/app/YouTubeApplication;)V

    .line 785
    :cond_0
    invoke-static {p0}, Lorg/ytlegacy/daviskas/app/prefetch/PrefetchService;->c(Landroid/content/Context;)V

    .line 786
    invoke-static {p0}, Lorg/ytlegacy/daviskas/app/compat/aj;->a(Landroid/content/Context;)V

    .line 788
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->n:Lorg/ytlegacy/daviskas/core/client/aq;

    invoke-virtual {v0}, Lorg/ytlegacy/daviskas/core/client/aq;->f()V

    .line 789
    return-void
.end method

.method protected final a()Z
    .locals 1
    const/4 v0, 0x1
    return v0
.end method

.method public final b()Lorg/ytlegacy/daviskas/core/client/bb;
    .locals 1

    .prologue
    .line 641
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->f:Lorg/ytlegacy/daviskas/core/client/t;

    return-object v0
.end method

.method public final bridge synthetic c()Lorg/ytlegacy/daviskas/core/c;
    .locals 1

    .prologue
    .line 107
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->a:Lorg/ytlegacy/daviskas/app/s;

    return-object v0
.end method

.method protected final d()Lorg/ytlegacy/daviskas/core/async/a;
    .locals 3

    .prologue
    .line 537
    const/4 v0, 0x3

    new-array v0, v0, [Ljava/lang/String;

    const/4 v1, 0x0

    const-string v2, "https://www.googleapis.com/auth/youtube"

    aput-object v2, v0, v1

    const/4 v1, 0x1

    const-string v2, "https://www.googleapis.com/auth/plus.me"

    aput-object v2, v0, v1

    const/4 v1, 0x2

    const-string v2, "https://www.googleapis.com/auth/emeraldsea.mobileapps.doritos.cookie"

    aput-object v2, v0, v1

    invoke-static {v0}, Lorg/ytlegacy/daviskas/core/utils/Util;->a([Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    .line 542
    new-instance v1, Lorg/ytlegacy/daviskas/core/async/GmsAccountManagerWrapper;

    invoke-virtual {p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->M()Ljava/util/concurrent/Executor;

    move-result-object v2

    invoke-direct {v1, p0, v0, v2}, Lorg/ytlegacy/daviskas/core/async/GmsAccountManagerWrapper;-><init>(Landroid/content/Context;Ljava/lang/String;Ljava/util/concurrent/Executor;)V

    return-object v1
.end method

.method public final e()Lorg/ytlegacy/daviskas/core/player/StatsTracker;
    .locals 1

    .prologue
    .line 646
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->v:Lorg/ytlegacy/daviskas/core/player/StatsTracker;

    return-object v0
.end method

.method protected final e_()V
    .locals 30

    .prologue
    .line 235
    new-instance v2, Lorg/ytlegacy/daviskas/app/s;

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->getContentResolver()Landroid/content/ContentResolver;

    move-result-object v3

    invoke-direct {v2, v3}, Lorg/ytlegacy/daviskas/app/s;-><init>(Landroid/content/ContentResolver;)V

    move-object/from16 v0, p0

    iput-object v2, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->a:Lorg/ytlegacy/daviskas/app/s;

    .line 238
    move-object/from16 v0, p0

    iget-object v2, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->a:Lorg/ytlegacy/daviskas/app/s;

    invoke-virtual {v2}, Lorg/ytlegacy/daviskas/app/s;->m()Z

    move-result v2

    if-eqz v2, :cond_0

    # PATCH: Disable PrefetchService components
    move-object/from16 v0, p0
    invoke-virtual {v0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->getPackageManager()Landroid/content/pm/PackageManager;
    move-result-object v1

    new-instance v2, Landroid/content/ComponentName;
    const-class v3, Lorg/ytlegacy/daviskas/app/prefetch/PrefetchService;
    invoke-direct {v2, v0, v3}, Landroid/content/ComponentName;-><init>(Landroid/content/Context;Ljava/lang/Class;)V

    const/4 v3, 0x2
    const/4 v4, 0x1
    invoke-virtual {v1, v2, v3, v4}, Landroid/content/pm/PackageManager;->setComponentEnabledSetting(Landroid/content/ComponentName;II)V

    new-instance v2, Landroid/content/ComponentName;
    const-class v5, Lorg/ytlegacy/daviskas/app/prefetch/PrefetchService$DeviceStateReceiver;
    invoke-direct {v2, v0, v5}, Landroid/content/ComponentName;-><init>(Landroid/content/Context;Ljava/lang/Class;)V
    invoke-virtual {v1, v2, v3, v4}, Landroid/content/pm/PackageManager;->setComponentEnabledSetting(Landroid/content/ComponentName;II)V

    :cond_0
    invoke-super/range {p0 .. p0}, Lorg/ytlegacy/daviskas/core/BaseApplication;->e_()V

    .line 243
    new-instance v2, Lorg/ytlegacy/daviskas/core/client/aw;

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->Q()Lorg/apache/http/impl/client/AbstractHttpClient;

    move-result-object v3

    move-object/from16 v0, p0

    iget-object v4, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->a:Lorg/ytlegacy/daviskas/app/s;

    invoke-direct {v2, v3, v4}, Lorg/ytlegacy/daviskas/core/client/aw;-><init>(Lorg/apache/http/client/HttpClient;Lorg/ytlegacy/daviskas/core/c;)V

    move-object/from16 v0, p0

    iput-object v2, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->u:Lorg/ytlegacy/daviskas/core/client/aw;

    .line 244
    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->Q()Lorg/apache/http/impl/client/AbstractHttpClient;

    move-result-object v2

    move-object/from16 v0, p0

    iget-object v3, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->u:Lorg/ytlegacy/daviskas/core/client/aw;

    invoke-virtual {v3}, Lorg/ytlegacy/daviskas/core/client/aw;->a()Lorg/ytlegacy/daviskas/core/utils/ap;

    move-result-object v3

    invoke-virtual {v2, v3}, Lorg/apache/http/impl/client/AbstractHttpClient;->setCookieStore(Lorg/apache/http/client/CookieStore;)V

    .line 246
    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->k()Lorg/ytlegacy/daviskas/core/utils/q;

    move-result-object v2

    invoke-interface {v2}, Lorg/ytlegacy/daviskas/core/utils/q;->g()Z

    move-result v2

    if-eqz v2, :cond_1

    new-instance v2, Landroid/content/ComponentName;

    const-string v3, "org.ytlegacy.daviskas.ManageNetworkUsageActivity"

    move-object/from16 v0, p0

    invoke-direct {v2, v0, v3}, Landroid/content/ComponentName;-><init>(Landroid/content/Context;Ljava/lang/String;)V

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->getPackageManager()Landroid/content/pm/PackageManager;

    move-result-object v3

    invoke-virtual {v3, v2}, Landroid/content/pm/PackageManager;->getComponentEnabledSetting(Landroid/content/ComponentName;)I

    move-result v4

    const/4 v5, 0x1

    if-eq v4, v5, :cond_1

    invoke-static {}, Lorg/ytlegacy/daviskas/core/L;->b()V

    const/4 v4, 0x1

    const/4 v5, 0x1

    invoke-virtual {v3, v2, v4, v5}, Landroid/content/pm/PackageManager;->setComponentEnabledSetting(Landroid/content/ComponentName;II)V

    .line 248
    :cond_1
    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->S()Landroid/content/SharedPreferences;

    move-result-object v29

    .line 249
    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->W()Z

    move-result v2

    if-eqz v2, :cond_2

    .line 250
    invoke-static/range {v29 .. v29}, Lorg/ytlegacy/daviskas/app/compat/ah;->a(Landroid/content/SharedPreferences;)Lorg/ytlegacy/daviskas/app/compat/ai;

    move-result-object v2

    const-string v3, "download_only_while_charging"

    const/4 v4, 0x1

    invoke-virtual {v2, v3, v4}, Lorg/ytlegacy/daviskas/app/compat/ai;->a(Ljava/lang/String;Z)Lorg/ytlegacy/daviskas/app/compat/ai;

    move-result-object v2

    const-string v3, "transfer_max_connections"

    const/4 v4, 0x3

    invoke-virtual {v2, v3, v4}, Lorg/ytlegacy/daviskas/app/compat/ai;->a(Ljava/lang/String;I)Lorg/ytlegacy/daviskas/app/compat/ai;

    move-result-object v2

    invoke-virtual {v2}, Lorg/ytlegacy/daviskas/app/compat/ai;->a()V

    .line 255
    :cond_2
    invoke-interface/range {v29 .. v30}, Landroid/content/SharedPreferences;->registerOnSharedPreferenceChangeListener(Landroid/content/SharedPreferences$OnSharedPreferenceChangeListener;)V

    .line 256
    new-instance v2, Lorg/ytlegacy/daviskas/app/a;

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->getApplicationContext()Landroid/content/Context;

    move-result-object v3

    move-object/from16 v0, v29

    invoke-direct {v2, v3, v0}, Lorg/ytlegacy/daviskas/app/a;-><init>(Landroid/content/Context;Landroid/content/SharedPreferences;)V

    move-object/from16 v0, p0

    iput-object v2, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->D:Lorg/ytlegacy/daviskas/app/a;

    .line 258
    invoke-static/range {p0 .. p0}, Lorg/ytlegacy/daviskas/core/utils/s;->d(Landroid/content/Context;)Ljava/lang/String;

    move-result-object v2

    move-object/from16 v0, v29

    invoke-static {v2, v0}, Lorg/ytlegacy/daviskas/core/utils/Util;->a(Ljava/lang/String;Landroid/content/SharedPreferences;)Lorg/ytlegacy/daviskas/core/utils/Util$StartupType;

    move-result-object v2

    .line 260
    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->getResources()Landroid/content/res/Resources;

    move-result-object v3

    const v4, 0x7f0a00ce

    invoke-virtual {v3, v4}, Landroid/content/res/Resources;->getString(I)Ljava/lang/String;

    move-result-object v3

    .line 261
    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->h()Lorg/ytlegacy/daviskas/core/Analytics;

    move-result-object v4

    const/4 v5, 0x3

    const-string v6, "FormFactor"

    const/4 v7, 0x2

    invoke-interface {v4, v5, v6, v3, v7}, Lorg/ytlegacy/daviskas/core/Analytics;->a(ILjava/lang/String;Ljava/lang/String;I)V

    .line 263
    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->h()Lorg/ytlegacy/daviskas/core/Analytics;

    move-result-object v3

    const-string v4, "Startup"

    invoke-virtual {v2}, Lorg/ytlegacy/daviskas/core/utils/Util$StartupType;->toString()Ljava/lang/String;

    move-result-object v2

    invoke-interface {v3, v4, v2}, Lorg/ytlegacy/daviskas/core/Analytics;->a(Ljava/lang/String;Ljava/lang/String;)V

    .line 265
    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->W()Z

    move-result v2

    if-eqz v2, :cond_3

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->getCacheDir()Ljava/io/File;

    move-result-object v2

    invoke-virtual {v2}, Ljava/io/File;->listFiles()[Ljava/io/File;

    move-result-object v2

    new-instance v3, Lorg/ytlegacy/daviskas/app/q;

    move-object/from16 v0, p0

    invoke-direct {v3, v0, v2}, Lorg/ytlegacy/daviskas/app/q;-><init>(Lorg/ytlegacy/daviskas/app/YouTubeApplication;[Ljava/io/File;)V

    invoke-virtual {v3}, Lorg/ytlegacy/daviskas/app/q;->start()V

    .line 267
    :cond_3
    new-instance v5, Lorg/ytlegacy/daviskas/core/utils/an;

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->getContentResolver()Landroid/content/ContentResolver;

    move-result-object v2

    invoke-direct {v5, v2}, Lorg/ytlegacy/daviskas/core/utils/an;-><init>(Landroid/content/ContentResolver;)V

    .line 268
    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->getContentResolver()Landroid/content/ContentResolver;

    move-result-object v2

    const-string v3, "android_id"

    invoke-static {v2, v3}, Landroid/provider/Settings$Secure;->getString(Landroid/content/ContentResolver;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v8

    .line 269
    invoke-static {v8}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v2

    if-eqz v2, :cond_4

    .line 272
    const-string v2, "%x"

    const/4 v3, 0x1

    new-array v3, v3, [Ljava/lang/Object;

    const/4 v4, 0x0

    new-instance v6, Ljava/util/Random;

    invoke-direct {v6}, Ljava/util/Random;-><init>()V

    invoke-virtual {v6}, Ljava/util/Random;->nextLong()J

    move-result-wide v6

    invoke-static {v6, v7}, Ljava/lang/Long;->valueOf(J)Ljava/lang/Long;

    move-result-object v6

    aput-object v6, v3, v4

    invoke-static {v2, v3}, Ljava/lang/String;->format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;

    move-result-object v8

    .line 274
    :cond_4
    new-instance v2, Lorg/ytlegacy/daviskas/core/client/s;

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->N()Ljava/util/concurrent/Executor;

    move-result-object v3

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->P()Lorg/apache/http/client/HttpClient;

    move-result-object v4

    sget-object v6, Lorg/ytlegacy/daviskas/app/g;->a:[B

    sget-object v7, Lorg/ytlegacy/daviskas/app/g;->b:[B

    invoke-direct/range {v2 .. v8}, Lorg/ytlegacy/daviskas/core/client/s;-><init>(Ljava/util/concurrent/Executor;Lorg/apache/http/client/HttpClient;Lorg/ytlegacy/daviskas/core/utils/an;[B[BLjava/lang/String;)V

    .line 281
    new-instance v21, Lorg/ytlegacy/daviskas/core/async/s;

    move-object/from16 v0, v21

    move-object/from16 v1, v29

    invoke-direct {v0, v2, v1}, Lorg/ytlegacy/daviskas/core/async/s;-><init>(Lorg/ytlegacy/daviskas/core/client/ap;Landroid/content/SharedPreferences;)V

    .line 284
    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->U()Lorg/ytlegacy/daviskas/core/async/UserAuthorizer;

    move-result-object v23

    .line 285
    new-instance v2, Ljava/util/concurrent/atomic/AtomicReference;

    const-string v3, "username"

    const/4 v4, 0x0

    move-object/from16 v0, v29

    invoke-interface {v0, v3, v4}, Landroid/content/SharedPreferences;->getString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v3

    invoke-direct {v2, v3}, Ljava/util/concurrent/atomic/AtomicReference;-><init>(Ljava/lang/Object;)V

    move-object/from16 v0, p0

    iput-object v2, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->z:Ljava/util/concurrent/atomic/AtomicReference;

    .line 286
    new-instance v2, Ljava/util/concurrent/atomic/AtomicReference;

    const-string v3, "user_channel_id"

    const/4 v4, 0x0

    move-object/from16 v0, v29

    invoke-interface {v0, v3, v4}, Landroid/content/SharedPreferences;->getString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v3

    invoke-direct {v2, v3}, Ljava/util/concurrent/atomic/AtomicReference;-><init>(Ljava/lang/Object;)V

    move-object/from16 v0, p0

    iput-object v2, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->A:Ljava/util/concurrent/atomic/AtomicReference;

    .line 288
    new-instance v2, Ljava/util/concurrent/atomic/AtomicReference;

    const-string v3, "user_account"

    const/4 v4, 0x0

    move-object/from16 v0, v29

    invoke-interface {v0, v3, v4}, Landroid/content/SharedPreferences;->getString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v3

    invoke-direct {v2, v3}, Ljava/util/concurrent/atomic/AtomicReference;-><init>(Ljava/lang/Object;)V

    move-object/from16 v0, p0

    iput-object v2, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->B:Ljava/util/concurrent/atomic/AtomicReference;

    .line 289
    move-object/from16 v0, v23

    move-object/from16 v1, p0

    invoke-virtual {v0, v1}, Lorg/ytlegacy/daviskas/core/async/UserAuthorizer;->a(Lorg/ytlegacy/daviskas/core/async/bu;)V

    .line 290
    const/4 v2, -0x2

    move-object/from16 v0, p0

    invoke-virtual {v0, v2}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->a(I)V

    .line 292
    new-instance v2, Lorg/ytlegacy/daviskas/core/client/ah;

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->getResources()Landroid/content/res/Resources;

    move-result-object v3

    const v4, 0x7f090021

    invoke-virtual {v3, v4}, Landroid/content/res/Resources;->getDimensionPixelSize(I)I

    move-result v3

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->getResources()Landroid/content/res/Resources;

    move-result-object v4

    const v5, 0x7f090022

    invoke-virtual {v4, v5}, Landroid/content/res/Resources;->getDimensionPixelSize(I)I

    move-result v4

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->getResources()Landroid/content/res/Resources;

    move-result-object v5

    const v6, 0x7f090027

    invoke-virtual {v5, v6}, Landroid/content/res/Resources;->getDimensionPixelSize(I)I

    move-result v5

    sget v6, Lorg/ytlegacy/daviskas/core/utils/Util;->a:I

    const/16 v7, 0xa

    if-le v6, v7, :cond_7

    const/4 v6, 0x1

    :goto_0
    const/4 v7, 0x1

    invoke-direct/range {v2 .. v7}, Lorg/ytlegacy/daviskas/core/client/ah;-><init>(IIIZZ)V

    .line 299
    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->N()Ljava/util/concurrent/Executor;

    move-result-object v9

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->O()Ljava/util/concurrent/Executor;

    move-result-object v10

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->P()Lorg/apache/http/client/HttpClient;

    move-result-object v11

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->getCacheDir()Ljava/io/File;

    move-result-object v3

    invoke-virtual {v3}, Ljava/io/File;->getAbsolutePath()Ljava/lang/String;

    move-result-object v12

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->L()Lorg/ytlegacy/daviskas/core/utils/aj;

    move-result-object v13

    invoke-static/range {p0 .. p0}, Lorg/ytlegacy/daviskas/core/utils/Util;->e(Landroid/content/Context;)Z

    move-result v15

    move-object v14, v2

    invoke-static/range {v9 .. v15}, Lorg/ytlegacy/daviskas/core/client/ag;->a(Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;Lorg/apache/http/client/HttpClient;Ljava/lang/String;Lorg/ytlegacy/daviskas/core/utils/f;Lorg/ytlegacy/daviskas/core/client/ah;Z)Lorg/ytlegacy/daviskas/core/client/ag;

    move-result-object v2

    move-object/from16 v0, p0

    iput-object v2, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->k:Lorg/ytlegacy/daviskas/core/client/ag;

    .line 308
    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->getResources()Landroid/content/res/Resources;

    move-result-object v2

    const v3, 0x7f0c0001

    invoke-virtual {v2, v3}, Landroid/content/res/Resources;->getInteger(I)I

    move-result v2

    .line 310
    new-instance v3, Lorg/ytlegacy/daviskas/core/async/aw;

    move-object/from16 v0, v29

    invoke-direct {v3, v0}, Lorg/ytlegacy/daviskas/core/async/aw;-><init>(Landroid/content/SharedPreferences;)V

    .line 311
    sput-object v29, Lorg/ytlegacy/daviskas/core/async/ap;->a:Landroid/content/SharedPreferences;

    .line 313
    new-instance v15, Lorg/ytlegacy/daviskas/core/async/GDataRequestFactory;

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->V()Lorg/ytlegacy/daviskas/core/utils/SafeSearch;

    move-result-object v4

    invoke-static/range {p0 .. p0}, Lorg/ytlegacy/daviskas/core/utils/Util;->a(Landroid/content/Context;)Ljava/lang/String;

    move-result-object v5

    invoke-direct {v15, v3, v2, v4, v5}, Lorg/ytlegacy/daviskas/core/async/GDataRequestFactory;-><init>(Lorg/ytlegacy/daviskas/core/async/ak;ILorg/ytlegacy/daviskas/core/utils/SafeSearch;Ljava/lang/String;)V

    .line 317
    new-instance v9, Lorg/ytlegacy/daviskas/core/client/t;

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->N()Ljava/util/concurrent/Executor;

    move-result-object v11

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->P()Lorg/apache/http/client/HttpClient;

    move-result-object v12

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->L()Lorg/ytlegacy/daviskas/core/utils/aj;

    move-result-object v13

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->R()Lorg/ytlegacy/daviskas/core/converter/m;

    move-result-object v14

    move-object/from16 v10, p0

    invoke-direct/range {v9 .. v15}, Lorg/ytlegacy/daviskas/core/client/t;-><init>(Landroid/content/Context;Ljava/util/concurrent/Executor;Lorg/apache/http/client/HttpClient;Lorg/ytlegacy/daviskas/core/utils/f;Lorg/ytlegacy/daviskas/core/converter/m;Lorg/ytlegacy/daviskas/core/async/GDataRequestFactory;)V

    move-object/from16 v0, p0

    iput-object v9, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->f:Lorg/ytlegacy/daviskas/core/client/t;

    .line 323
    move-object/from16 v0, p0

    iget-object v2, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->a:Lorg/ytlegacy/daviskas/app/s;

    invoke-virtual {v2}, Lorg/ytlegacy/daviskas/app/s;->r()Z

    move-result v2

    if-eqz v2, :cond_8

    .line 324
    move-object/from16 v0, p0

    iget-object v2, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->f:Lorg/ytlegacy/daviskas/core/client/t;

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->T()Lorg/ytlegacy/daviskas/core/async/a;

    move-result-object v3

    move-object/from16 v0, p0

    iget-object v4, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->a:Lorg/ytlegacy/daviskas/app/s;

    invoke-virtual {v4}, Lorg/ytlegacy/daviskas/app/s;->K()Lorg/ytlegacy/daviskas/core/async/GDataRequest$Version;

    move-result-object v4

    move-object/from16 v0, v21

    move-object/from16 v1, v23

    invoke-virtual {v2, v0, v3, v4, v1}, Lorg/ytlegacy/daviskas/core/client/t;->a(Lorg/ytlegacy/daviskas/core/async/DeviceAuthorizer;Lorg/ytlegacy/daviskas/core/async/a;Lorg/ytlegacy/daviskas/core/async/GDataRequest$Version;Lorg/ytlegacy/daviskas/core/async/UserAuthorizer;)V

    .line 329
    :goto_1
    move-object/from16 v0, p0

    iget-object v2, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->f:Lorg/ytlegacy/daviskas/core/client/t;

    move-object/from16 v0, v23

    invoke-virtual {v0, v2}, Lorg/ytlegacy/daviskas/core/async/UserAuthorizer;->a(Lorg/ytlegacy/daviskas/core/client/bb;)V

    .line 331
    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->g()Lorg/ytlegacy/daviskas/core/player/bi;

    move-result-object v2

    instance-of v2, v2, Lorg/ytlegacy/daviskas/app/prefetch/k;

    if-eqz v2, :cond_5

    .line 332
    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->g()Lorg/ytlegacy/daviskas/core/player/bi;

    move-result-object v2

    check-cast v2, Lorg/ytlegacy/daviskas/app/prefetch/k;

    .line 333
    move-object/from16 v0, p0

    iget-object v3, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->f:Lorg/ytlegacy/daviskas/core/client/t;

    invoke-virtual {v2, v3}, Lorg/ytlegacy/daviskas/app/prefetch/k;->a(Lorg/ytlegacy/daviskas/core/client/bb;)V

    .line 334
    move-object/from16 v0, v23

    invoke-virtual {v2, v0}, Lorg/ytlegacy/daviskas/app/prefetch/k;->a(Lorg/ytlegacy/daviskas/core/async/UserAuthorizer;)V

    .line 337
    :cond_5
    new-instance v2, Lorg/ytlegacy/daviskas/core/async/UserDelegator;

    move-object/from16 v0, p0

    iget-object v3, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->f:Lorg/ytlegacy/daviskas/core/client/t;

    move-object/from16 v0, p0

    iget-object v4, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->k:Lorg/ytlegacy/daviskas/core/client/ag;

    move-object/from16 v0, v29

    invoke-direct {v2, v3, v4, v0}, Lorg/ytlegacy/daviskas/core/async/UserDelegator;-><init>(Lorg/ytlegacy/daviskas/core/client/bb;Lorg/ytlegacy/daviskas/core/client/bd;Landroid/content/SharedPreferences;)V

    move-object/from16 v0, p0

    iput-object v2, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->e:Lorg/ytlegacy/daviskas/core/async/UserDelegator;

    .line 338
    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->T()Lorg/ytlegacy/daviskas/core/async/a;

    move-result-object v2

    move-object/from16 v0, p0

    iget-object v3, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->e:Lorg/ytlegacy/daviskas/core/async/UserDelegator;

    invoke-virtual {v2, v3}, Lorg/ytlegacy/daviskas/core/async/a;->a(Lorg/ytlegacy/daviskas/core/async/UserDelegator;)V

    .line 340
    new-instance v2, Lorg/ytlegacy/daviskas/core/client/al;

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->N()Ljava/util/concurrent/Executor;

    move-result-object v3

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->getCacheDir()Ljava/io/File;

    move-result-object v4

    invoke-virtual {v4}, Ljava/io/File;->getAbsolutePath()Ljava/lang/String;

    move-result-object v4

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->L()Lorg/ytlegacy/daviskas/core/utils/aj;

    move-result-object v5

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->P()Lorg/apache/http/client/HttpClient;

    move-result-object v6

    const-string v7, "AIzaSyA8eiZmM1FaDVjRy-df2KTyQ_vz_yYM39w"

    invoke-direct/range {v2 .. v7}, Lorg/ytlegacy/daviskas/core/client/al;-><init>(Ljava/util/concurrent/Executor;Ljava/lang/String;Lorg/ytlegacy/daviskas/core/utils/f;Lorg/apache/http/client/HttpClient;Ljava/lang/String;)V

    move-object/from16 v0, p0

    iput-object v2, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->g:Lorg/ytlegacy/daviskas/core/client/al;

    .line 347
    move-object/from16 v0, p0

    iget-object v2, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->a:Lorg/ytlegacy/daviskas/app/s;

    invoke-virtual {v2}, Lorg/ytlegacy/daviskas/app/s;->v()Z

    move-result v2

    if-eqz v2, :cond_9

    .line 348
    new-instance v2, Lorg/ytlegacy/daviskas/core/client/bm;

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->L()Lorg/ytlegacy/daviskas/core/utils/aj;

    move-result-object v3

    invoke-direct {v2, v3}, Lorg/ytlegacy/daviskas/core/client/bm;-><init>(Lorg/ytlegacy/daviskas/core/utils/f;)V

    move-object/from16 v0, p0

    iput-object v2, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->t:Lorg/ytlegacy/daviskas/core/client/bm;

    .line 353
    :goto_2
    new-instance v9, Lorg/ytlegacy/daviskas/core/client/l;

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->N()Ljava/util/concurrent/Executor;

    move-result-object v10

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->Q()Lorg/apache/http/impl/client/AbstractHttpClient;

    move-result-object v11

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->R()Lorg/ytlegacy/daviskas/core/converter/m;

    move-result-object v12

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->L()Lorg/ytlegacy/daviskas/core/utils/aj;

    move-result-object v13

    move-object/from16 v0, p0

    iget-object v15, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->f:Lorg/ytlegacy/daviskas/core/client/t;

    move-object/from16 v14, v29

    invoke-direct/range {v9 .. v15}, Lorg/ytlegacy/daviskas/core/client/l;-><init>(Ljava/util/concurrent/Executor;Lorg/apache/http/client/HttpClient;Lorg/ytlegacy/daviskas/core/converter/m;Lorg/ytlegacy/daviskas/core/utils/f;Landroid/content/SharedPreferences;Lorg/ytlegacy/daviskas/core/client/bb;)V

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->g()Lorg/ytlegacy/daviskas/core/player/bi;

    move-result-object v4

    invoke-static/range {p0 .. p0}, Lorg/ytlegacy/daviskas/core/utils/s;->d(Landroid/content/Context;)Ljava/lang/String;

    move-result-object v5

    move-object/from16 v0, p0

    iget-object v2, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->a:Lorg/ytlegacy/daviskas/app/s;

    invoke-virtual {v2}, Lorg/ytlegacy/daviskas/app/s;->L()Ljava/lang/String;

    move-result-object v6

    move-object/from16 v0, p0

    iget-object v2, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->a:Lorg/ytlegacy/daviskas/app/s;

    invoke-virtual {v2}, Lorg/ytlegacy/daviskas/app/s;->M()Ljava/lang/String;

    move-result-object v7

    const-string v2, "context cannot be null"

    move-object/from16 v0, p0

    invoke-static {v0, v2}, Lorg/ytlegacy/daviskas/core/utils/u;->a(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    const-string v2, "streamSelector cannot be null"

    invoke-static {v4, v2}, Lorg/ytlegacy/daviskas/core/utils/u;->a(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    const-string v2, "clientVersion cannot be null"

    invoke-static {v5, v2}, Lorg/ytlegacy/daviskas/core/utils/u;->a(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    const-string v2, "adsenseDomain cannot be null"

    invoke-static {v6, v2}, Lorg/ytlegacy/daviskas/core/utils/u;->a(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    const-string v2, "adsenseAdsPath cannot be null"

    invoke-static {v7, v2}, Lorg/ytlegacy/daviskas/core/utils/u;->a(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    new-instance v2, Lorg/ytlegacy/daviskas/core/utils/a;

    move-object/from16 v3, p0

    invoke-direct/range {v2 .. v7}, Lorg/ytlegacy/daviskas/core/utils/a;-><init>(Landroid/content/Context;Lorg/ytlegacy/daviskas/core/player/bi;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V

    iput-object v2, v9, Lorg/ytlegacy/daviskas/core/client/l;->b:Lorg/ytlegacy/daviskas/core/utils/a;

    move-object/from16 v0, p0

    iget-object v2, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->A:Ljava/util/concurrent/atomic/AtomicReference;

    invoke-virtual {v9, v2}, Lorg/ytlegacy/daviskas/core/client/l;->a(Ljava/util/concurrent/atomic/AtomicReference;)Lorg/ytlegacy/daviskas/core/client/l;

    move-result-object v2

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->n()Ljava/lang/String;

    move-result-object v3

    invoke-virtual {v2, v3}, Lorg/ytlegacy/daviskas/core/client/l;->b(Ljava/lang/String;)Lorg/ytlegacy/daviskas/core/client/l;

    move-result-object v2

    invoke-static/range {p0 .. p0}, Lorg/ytlegacy/daviskas/core/utils/Util;->a(Landroid/content/Context;)Ljava/lang/String;

    move-result-object v3

    invoke-virtual {v2, v3}, Lorg/ytlegacy/daviskas/core/client/l;->c(Ljava/lang/String;)Lorg/ytlegacy/daviskas/core/client/l;

    move-result-object v2

    move-object/from16 v0, p0

    iget-object v3, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->u:Lorg/ytlegacy/daviskas/core/client/aw;

    invoke-virtual {v3}, Lorg/ytlegacy/daviskas/core/client/aw;->a()Lorg/ytlegacy/daviskas/core/utils/ap;

    move-result-object v3

    invoke-virtual {v2, v3}, Lorg/ytlegacy/daviskas/core/client/l;->a(Lorg/ytlegacy/daviskas/core/utils/ap;)Lorg/ytlegacy/daviskas/core/client/l;

    move-result-object v2

    move-object/from16 v0, p0

    iget-object v3, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->a:Lorg/ytlegacy/daviskas/app/s;

    invoke-virtual {v3}, Lorg/ytlegacy/daviskas/app/s;->O()Z

    move-result v3

    invoke-virtual {v2, v3}, Lorg/ytlegacy/daviskas/core/client/l;->b(Z)Lorg/ytlegacy/daviskas/core/client/l;

    move-result-object v2

    move-object/from16 v0, p0

    iget-object v3, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->t:Lorg/ytlegacy/daviskas/core/client/bm;

    invoke-virtual {v2, v3}, Lorg/ytlegacy/daviskas/core/client/l;->a(Lorg/ytlegacy/daviskas/core/client/bm;)Lorg/ytlegacy/daviskas/core/client/l;

    move-result-object v2

    invoke-virtual {v2}, Lorg/ytlegacy/daviskas/core/client/l;->a()Lorg/ytlegacy/daviskas/core/client/j;

    move-result-object v2

    move-object/from16 v0, p0

    iput-object v2, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->h:Lorg/ytlegacy/daviskas/core/client/j;

    .line 374
    invoke-static/range {p0 .. p0}, Lorg/ytlegacy/daviskas/core/utils/s;->d(Landroid/content/Context;)Ljava/lang/String;

    move-result-object v2

    invoke-static {v2}, Lorg/ytlegacy/daviskas/core/player/f;->a(Ljava/lang/String;)V

    .line 375
    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->L()Lorg/ytlegacy/daviskas/core/utils/aj;

    move-result-object v2

    invoke-static {v2}, Lorg/ytlegacy/daviskas/core/player/f;->a(Lorg/ytlegacy/daviskas/core/utils/f;)V

    .line 376
    move-object/from16 v0, p0

    iget-object v2, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->t:Lorg/ytlegacy/daviskas/core/client/bm;

    invoke-static {v2}, Lorg/ytlegacy/daviskas/core/player/f;->a(Lorg/ytlegacy/daviskas/core/client/bm;)V

    .line 378
    new-instance v2, Lorg/ytlegacy/daviskas/core/client/am;

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->N()Ljava/util/concurrent/Executor;

    move-result-object v3

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->P()Lorg/apache/http/client/HttpClient;

    move-result-object v4

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->R()Lorg/ytlegacy/daviskas/core/converter/m;

    move-result-object v5

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->getCacheDir()Ljava/io/File;

    move-result-object v6

    invoke-virtual {v6}, Ljava/io/File;->getAbsolutePath()Ljava/lang/String;

    move-result-object v6

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->L()Lorg/ytlegacy/daviskas/core/utils/aj;

    move-result-object v7

    invoke-direct/range {v2 .. v7}, Lorg/ytlegacy/daviskas/core/client/am;-><init>(Ljava/util/concurrent/Executor;Lorg/apache/http/client/HttpClient;Lorg/ytlegacy/daviskas/core/converter/m;Ljava/lang/String;Lorg/ytlegacy/daviskas/core/utils/f;)V

    move-object/from16 v0, p0

    iput-object v2, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->i:Lorg/ytlegacy/daviskas/core/client/am;

    .line 385
    new-instance v9, Lorg/ytlegacy/daviskas/core/client/ai;

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->N()Ljava/util/concurrent/Executor;

    move-result-object v10

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->getCacheDir()Ljava/io/File;

    move-result-object v2

    invoke-virtual {v2}, Ljava/io/File;->getAbsolutePath()Ljava/lang/String;

    move-result-object v11

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->L()Lorg/ytlegacy/daviskas/core/utils/aj;

    move-result-object v12

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->P()Lorg/apache/http/client/HttpClient;

    move-result-object v13

    const-string v14, "AIzaSyA8eiZmM1FaDVjRy-df2KTyQ_vz_yYM39w"

    invoke-static/range {p0 .. p0}, Lorg/ytlegacy/daviskas/core/utils/Util;->a(Landroid/content/Context;)Ljava/lang/String;

    move-result-object v15

    invoke-direct/range {v9 .. v15}, Lorg/ytlegacy/daviskas/core/client/ai;-><init>(Ljava/util/concurrent/Executor;Ljava/lang/String;Lorg/ytlegacy/daviskas/core/utils/f;Lorg/apache/http/client/HttpClient;Ljava/lang/String;Ljava/lang/String;)V

    move-object/from16 v0, p0

    iput-object v9, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->j:Lorg/ytlegacy/daviskas/core/client/ai;

    .line 393
    new-instance v14, Lorg/ytlegacy/daviskas/core/client/StatParams;

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->getPackageName()Ljava/lang/String;

    move-result-object v3

    invoke-static/range {p0 .. p0}, Lorg/ytlegacy/daviskas/core/utils/s;->d(Landroid/content/Context;)Ljava/lang/String;

    move-result-object v4

    invoke-static/range {p0 .. p0}, Lorg/ytlegacy/daviskas/core/utils/k;->a(Landroid/content/Context;)Z

    move-result v2

    if-eqz v2, :cond_a

    sget-object v2, Lorg/ytlegacy/daviskas/core/client/StatParams$Platform;->TABLET:Lorg/ytlegacy/daviskas/core/client/StatParams$Platform;

    :goto_3
    sget-object v5, Lorg/ytlegacy/daviskas/core/client/StatParams$SoftwareInterface;->ANDROID:Lorg/ytlegacy/daviskas/core/client/StatParams$SoftwareInterface;

    invoke-direct {v14, v3, v4, v2, v5}, Lorg/ytlegacy/daviskas/core/client/StatParams;-><init>(Ljava/lang/String;Ljava/lang/String;Lorg/ytlegacy/daviskas/core/client/StatParams$Platform;Lorg/ytlegacy/daviskas/core/client/StatParams$SoftwareInterface;)V

    .line 399
    new-instance v2, Lorg/ytlegacy/daviskas/app/a/a;

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->S()Landroid/content/SharedPreferences;

    move-result-object v3

    move-object/from16 v0, p0

    iget-object v4, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->w:Lorg/ytlegacy/daviskas/app/prefetch/e;

    invoke-direct {v2, v3, v4}, Lorg/ytlegacy/daviskas/app/a/a;-><init>(Landroid/content/SharedPreferences;Lorg/ytlegacy/daviskas/app/prefetch/e;)V

    move-object/from16 v0, p0

    iput-object v2, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->x:Lorg/ytlegacy/daviskas/app/a/a;

    new-instance v2, Lorg/ytlegacy/daviskas/core/client/i;

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->P()Lorg/apache/http/client/HttpClient;

    move-result-object v3

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->N()Ljava/util/concurrent/Executor;

    move-result-object v4

    invoke-direct {v2, v3, v4}, Lorg/ytlegacy/daviskas/core/client/i;-><init>(Lorg/apache/http/client/HttpClient;Ljava/util/concurrent/Executor;)V

    new-instance v3, Lorg/ytlegacy/daviskas/core/client/bh;

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->P()Lorg/apache/http/client/HttpClient;

    move-result-object v4

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->N()Ljava/util/concurrent/Executor;

    move-result-object v5

    invoke-direct {v3, v4, v5}, Lorg/ytlegacy/daviskas/core/client/bh;-><init>(Lorg/apache/http/client/HttpClient;Ljava/util/concurrent/Executor;)V

    new-instance v20, Lorg/ytlegacy/daviskas/core/client/bi;

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->P()Lorg/apache/http/client/HttpClient;

    move-result-object v4

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->N()Ljava/util/concurrent/Executor;

    move-result-object v5

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->L()Lorg/ytlegacy/daviskas/core/utils/aj;

    move-result-object v6

    move-object/from16 v0, v20

    move-object/from16 v1, v21

    invoke-direct {v0, v4, v1, v5, v6}, Lorg/ytlegacy/daviskas/core/client/bi;-><init>(Lorg/apache/http/client/HttpClient;Lorg/ytlegacy/daviskas/core/async/DeviceAuthorizer;Ljava/util/concurrent/Executor;Lorg/ytlegacy/daviskas/core/utils/f;)V

    new-instance v9, Lorg/ytlegacy/daviskas/app/a/c;

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->P()Lorg/apache/http/client/HttpClient;

    move-result-object v10

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->N()Ljava/util/concurrent/Executor;

    move-result-object v12

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->L()Lorg/ytlegacy/daviskas/core/utils/aj;

    move-result-object v13

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->T()Lorg/ytlegacy/daviskas/core/async/a;

    move-result-object v15

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->U()Lorg/ytlegacy/daviskas/core/async/UserAuthorizer;

    move-result-object v16

    move-object/from16 v0, p0

    iget-object v0, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->t:Lorg/ytlegacy/daviskas/core/client/bm;

    move-object/from16 v17, v0

    move-object/from16 v0, p0

    iget-object v0, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->w:Lorg/ytlegacy/daviskas/app/prefetch/e;

    move-object/from16 v18, v0

    move-object/from16 v0, p0

    iget-object v0, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->x:Lorg/ytlegacy/daviskas/app/a/a;

    move-object/from16 v19, v0

    move-object/from16 v11, v21

    invoke-direct/range {v9 .. v19}, Lorg/ytlegacy/daviskas/app/a/c;-><init>(Lorg/apache/http/client/HttpClient;Lorg/ytlegacy/daviskas/core/async/DeviceAuthorizer;Ljava/util/concurrent/Executor;Lorg/ytlegacy/daviskas/core/utils/f;Lorg/ytlegacy/daviskas/core/client/StatParams;Lorg/ytlegacy/daviskas/core/async/a;Lorg/ytlegacy/daviskas/core/async/UserAuthorizer;Lorg/ytlegacy/daviskas/core/client/bm;Lorg/ytlegacy/daviskas/app/prefetch/e;Lorg/ytlegacy/daviskas/app/a/a;)V

    new-instance v15, Lorg/ytlegacy/daviskas/core/player/StatsTracker;

    new-instance v16, Ljava/security/SecureRandom;

    invoke-direct/range {v16 .. v16}, Ljava/security/SecureRandom;-><init>()V

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->k()Lorg/ytlegacy/daviskas/core/utils/q;

    move-result-object v17

    move-object/from16 v18, v2

    move-object/from16 v19, v3

    move-object/from16 v21, v9

    invoke-direct/range {v15 .. v21}, Lorg/ytlegacy/daviskas/core/player/StatsTracker;-><init>(Ljava/util/Random;Lorg/ytlegacy/daviskas/core/utils/q;Lorg/ytlegacy/daviskas/core/client/i;Lorg/ytlegacy/daviskas/core/client/bh;Lorg/ytlegacy/daviskas/core/client/bi;Lorg/ytlegacy/daviskas/core/client/an;)V

    move-object/from16 v0, p0

    iput-object v15, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->v:Lorg/ytlegacy/daviskas/core/player/StatsTracker;

    .line 401
    new-instance v15, Lorg/ytlegacy/daviskas/app/p;

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->N()Ljava/util/concurrent/Executor;

    move-result-object v18

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->P()Lorg/apache/http/client/HttpClient;

    move-result-object v19

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->L()Lorg/ytlegacy/daviskas/core/utils/aj;

    move-result-object v20

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->S()Landroid/content/SharedPreferences;

    move-result-object v21

    move-object/from16 v0, p0

    iget-object v0, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->f:Lorg/ytlegacy/daviskas/core/client/t;

    move-object/from16 v22, v0

    move-object/from16 v0, p0

    iget-object v0, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->a:Lorg/ytlegacy/daviskas/app/s;

    move-object/from16 v24, v0

    move-object/from16 v16, p0

    move-object/from16 v17, p0

    move-object/from16 v25, v14

    invoke-direct/range {v15 .. v25}, Lorg/ytlegacy/daviskas/app/p;-><init>(Lorg/ytlegacy/daviskas/app/YouTubeApplication;Landroid/content/Context;Ljava/util/concurrent/Executor;Lorg/apache/http/client/HttpClient;Lorg/ytlegacy/daviskas/core/utils/f;Landroid/content/SharedPreferences;Lorg/ytlegacy/daviskas/core/client/bb;Lorg/ytlegacy/daviskas/core/async/UserAuthorizer;Lorg/ytlegacy/daviskas/core/c;Lorg/ytlegacy/daviskas/core/client/StatParams;)V

    move-object/from16 v0, p0

    iput-object v15, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->n:Lorg/ytlegacy/daviskas/core/client/aq;

    .line 417
    new-instance v22, Lorg/ytlegacy/daviskas/core/async/ck;

    move-object/from16 v0, p0

    iget-object v0, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->f:Lorg/ytlegacy/daviskas/core/client/t;

    move-object/from16 v24, v0

    move-object/from16 v0, p0

    iget-object v0, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->g:Lorg/ytlegacy/daviskas/core/client/al;

    move-object/from16 v25, v0

    move-object/from16 v0, p0

    iget-object v0, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->k:Lorg/ytlegacy/daviskas/core/client/ag;

    move-object/from16 v26, v0

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->T()Lorg/ytlegacy/daviskas/core/async/a;

    move-result-object v27

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->h()Lorg/ytlegacy/daviskas/core/Analytics;

    move-result-object v28

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->i()Lorg/ytlegacy/daviskas/core/e;

    invoke-direct/range {v22 .. v28}, Lorg/ytlegacy/daviskas/core/async/ck;-><init>(Lorg/ytlegacy/daviskas/core/async/UserAuthorizer;Lorg/ytlegacy/daviskas/core/client/bb;Lorg/ytlegacy/daviskas/core/client/bg;Lorg/ytlegacy/daviskas/core/client/bd;Lorg/ytlegacy/daviskas/core/async/a;Lorg/ytlegacy/daviskas/core/Analytics;)V

    move-object/from16 v0, v22

    move-object/from16 v1, p0

    iput-object v0, v1, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->d:Lorg/ytlegacy/daviskas/core/async/ck;

    .line 426
    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->X()Ljava/lang/String;

    move-result-object v2

    invoke-static {v2}, Lcom/google/android/ytremote/a/d/a;->a(Ljava/lang/String;)V

    .line 427
    new-instance v19, Lorg/ytlegacy/daviskas/app/remote/bn;

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->k()Lorg/ytlegacy/daviskas/core/utils/q;

    move-result-object v21

    move-object/from16 v0, p0

    iget-object v0, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->f:Lorg/ytlegacy/daviskas/core/client/t;

    move-object/from16 v24, v0

    move-object/from16 v0, p0

    iget-object v2, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->a:Lorg/ytlegacy/daviskas/app/s;

    invoke-virtual {v2}, Lorg/ytlegacy/daviskas/app/s;->o()Z

    move-result v25

    move-object/from16 v0, p0

    iget-object v2, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->a:Lorg/ytlegacy/daviskas/app/s;

    invoke-virtual {v2}, Lorg/ytlegacy/daviskas/app/s;->p()Z

    move-result v26

    move-object/from16 v0, p0

    iget-object v0, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->D:Lorg/ytlegacy/daviskas/app/a;

    move-object/from16 v27, v0

    move-object/from16 v20, p0

    move-object/from16 v22, v29

    invoke-direct/range {v19 .. v27}, Lorg/ytlegacy/daviskas/app/remote/bn;-><init>(Landroid/content/Context;Lorg/ytlegacy/daviskas/core/utils/q;Landroid/content/SharedPreferences;Lorg/ytlegacy/daviskas/core/async/UserAuthorizer;Lorg/ytlegacy/daviskas/core/client/bb;ZZLorg/ytlegacy/daviskas/app/a;)V

    move-object/from16 v0, v19

    move-object/from16 v1, p0

    iput-object v0, v1, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->l:Lorg/ytlegacy/daviskas/app/remote/bn;

    .line 438
    new-instance v2, Lorg/ytlegacy/daviskas/app/remote/w;

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->N()Ljava/util/concurrent/Executor;

    move-result-object v3

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->getResources()Landroid/content/res/Resources;

    move-result-object v4

    move-object/from16 v0, p0

    iget-object v5, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->l:Lorg/ytlegacy/daviskas/app/remote/bn;

    move-object/from16 v0, v29

    invoke-direct {v2, v3, v0, v4, v5}, Lorg/ytlegacy/daviskas/app/remote/w;-><init>(Ljava/util/concurrent/Executor;Landroid/content/SharedPreferences;Landroid/content/res/Resources;Lorg/ytlegacy/daviskas/app/remote/bn;)V

    move-object/from16 v0, p0

    iput-object v2, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->m:Lorg/ytlegacy/daviskas/app/remote/ca;

    .line 440
    new-instance v9, Lorg/ytlegacy/daviskas/app/remote/cb;

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->N()Ljava/util/concurrent/Executor;

    move-result-object v10

    move-object/from16 v0, p0

    iget-object v11, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->m:Lorg/ytlegacy/daviskas/app/remote/ca;

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->k()Lorg/ytlegacy/daviskas/core/utils/q;

    move-result-object v12

    move-object/from16 v0, p0

    iget-object v2, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->a:Lorg/ytlegacy/daviskas/app/s;

    invoke-virtual {v2}, Lorg/ytlegacy/daviskas/app/s;->n()Z

    move-result v13

    move-object/from16 v0, p0

    iget-object v14, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->l:Lorg/ytlegacy/daviskas/app/remote/bn;

    move-object/from16 v0, p0

    iget-object v2, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->a:Lorg/ytlegacy/daviskas/app/s;

    invoke-virtual {v2}, Lorg/ytlegacy/daviskas/app/s;->p()Z

    move-result v16

    move-object/from16 v0, p0

    iget-object v0, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->D:Lorg/ytlegacy/daviskas/app/a;

    move-object/from16 v17, v0

    move-object/from16 v15, v29

    invoke-direct/range {v9 .. v17}, Lorg/ytlegacy/daviskas/app/remote/cb;-><init>(Ljava/util/concurrent/Executor;Lorg/ytlegacy/daviskas/app/remote/ca;Lorg/ytlegacy/daviskas/core/utils/q;ZLorg/ytlegacy/daviskas/app/remote/bn;Landroid/content/SharedPreferences;ZLorg/ytlegacy/daviskas/app/a;)V

    move-object/from16 v0, p0

    iput-object v9, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->o:Lorg/ytlegacy/daviskas/app/remote/cb;

    .line 444
    new-instance v2, Lorg/ytlegacy/daviskas/app/remote/AtHomeConnection;

    move-object/from16 v0, p0

    invoke-direct {v2, v0}, Lorg/ytlegacy/daviskas/app/remote/AtHomeConnection;-><init>(Landroid/content/Context;)V

    move-object/from16 v0, p0

    iput-object v2, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->q:Lorg/ytlegacy/daviskas/app/remote/AtHomeConnection;

    .line 445
    new-instance v2, Lorg/ytlegacy/daviskas/app/remote/e;

    move-object/from16 v0, p0

    iget-object v3, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->q:Lorg/ytlegacy/daviskas/app/remote/AtHomeConnection;

    move-object/from16 v0, p0

    iget-object v4, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->f:Lorg/ytlegacy/daviskas/core/client/t;

    move-object/from16 v0, p0

    move-object/from16 v1, v23

    invoke-direct {v2, v0, v3, v1, v4}, Lorg/ytlegacy/daviskas/app/remote/e;-><init>(Landroid/content/Context;Lorg/ytlegacy/daviskas/app/remote/AtHomeConnection;Lorg/ytlegacy/daviskas/core/async/UserAuthorizer;Lorg/ytlegacy/daviskas/core/client/bb;)V

    move-object/from16 v0, p0

    iput-object v2, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->p:Lorg/ytlegacy/daviskas/app/remote/e;

    .line 448
    new-instance v2, Landroid/provider/SearchRecentSuggestions;

    const-string v3, "org.ytlegacy.daviskas.SuggestionProvider"

    const/4 v4, 0x1

    move-object/from16 v0, p0

    invoke-direct {v2, v0, v3, v4}, Landroid/provider/SearchRecentSuggestions;-><init>(Landroid/content/Context;Ljava/lang/String;I)V

    move-object/from16 v0, p0

    iput-object v2, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->b:Landroid/provider/SearchRecentSuggestions;

    .line 452
    invoke-direct/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->Z()Z

    move-result v2

    if-eqz v2, :cond_6

    .line 453
    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->I()Ljava/lang/String;

    move-result-object v2

    if-eqz v2, :cond_b

    .line 454
    invoke-static/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/autosync/a;->a(Lorg/ytlegacy/daviskas/app/YouTubeApplication;)V

    .line 460
    :cond_6
    :goto_4
    new-instance v9, Lorg/ytlegacy/daviskas/app/remote/ae;

    move-object/from16 v0, p0

    iget-object v11, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->q:Lorg/ytlegacy/daviskas/app/remote/AtHomeConnection;

    move-object/from16 v0, p0

    iget-object v12, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->p:Lorg/ytlegacy/daviskas/app/remote/e;

    move-object/from16 v0, p0

    iget-object v13, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->o:Lorg/ytlegacy/daviskas/app/remote/cb;

    move-object/from16 v0, p0

    iget-object v14, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->l:Lorg/ytlegacy/daviskas/app/remote/bn;

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->h()Lorg/ytlegacy/daviskas/core/Analytics;

    move-result-object v15

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->i()Lorg/ytlegacy/daviskas/core/e;

    move-result-object v16

    move-object/from16 v0, p0

    iget-object v0, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->D:Lorg/ytlegacy/daviskas/app/a;

    move-object/from16 v17, v0

    move-object/from16 v0, p0

    iget-object v2, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->a:Lorg/ytlegacy/daviskas/app/s;

    invoke-virtual {v2}, Lorg/ytlegacy/daviskas/app/s;->p()Z

    move-result v18

    move-object/from16 v10, p0

    invoke-direct/range {v9 .. v18}, Lorg/ytlegacy/daviskas/app/remote/ae;-><init>(Landroid/content/Context;Lorg/ytlegacy/daviskas/app/remote/AtHomeConnection;Lorg/ytlegacy/daviskas/app/remote/e;Lorg/ytlegacy/daviskas/app/remote/cb;Lorg/ytlegacy/daviskas/app/remote/bn;Lorg/ytlegacy/daviskas/core/Analytics;Lorg/ytlegacy/daviskas/core/e;Lorg/ytlegacy/daviskas/app/a;Z)V

    move-object/from16 v0, p0

    iput-object v9, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->y:Lorg/ytlegacy/daviskas/app/remote/ae;

    .line 470
    new-instance v18, Lorg/ytlegacy/daviskas/app/remote/az;

    move-object/from16 v0, p0

    iget-object v0, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->y:Lorg/ytlegacy/daviskas/app/remote/ae;

    move-object/from16 v20, v0

    move-object/from16 v0, p0

    iget-object v0, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->f:Lorg/ytlegacy/daviskas/core/client/t;

    move-object/from16 v21, v0

    move-object/from16 v0, p0

    iget-object v0, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->k:Lorg/ytlegacy/daviskas/core/client/ag;

    move-object/from16 v22, v0

    invoke-static/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/remote/ar;->a(Landroid/content/Context;)Lorg/ytlegacy/daviskas/app/remote/ar;

    move-result-object v24

    move-object/from16 v0, p0

    iget-object v2, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->a:Lorg/ytlegacy/daviskas/app/s;

    invoke-virtual {v2}, Lorg/ytlegacy/daviskas/app/s;->q()Z

    move-result v25

    move-object/from16 v19, p0

    invoke-direct/range {v18 .. v25}, Lorg/ytlegacy/daviskas/app/remote/az;-><init>(Landroid/content/Context;Lorg/ytlegacy/daviskas/app/remote/ae;Lorg/ytlegacy/daviskas/core/client/bb;Lorg/ytlegacy/daviskas/core/client/bd;Lorg/ytlegacy/daviskas/core/async/UserAuthorizer;Lorg/ytlegacy/daviskas/app/remote/ar;Z)V

    .line 479
    new-instance v2, Lorg/ytlegacy/daviskas/datalib/v3/a;

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->P()Lorg/apache/http/client/HttpClient;

    move-result-object v3

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->U()Lorg/ytlegacy/daviskas/core/async/UserAuthorizer;

    move-result-object v4

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->L()Lorg/ytlegacy/daviskas/core/utils/aj;

    move-result-object v5

    move-object/from16 v0, v29

    invoke-direct {v2, v3, v4, v0, v5}, Lorg/ytlegacy/daviskas/datalib/v3/a;-><init>(Lorg/apache/http/client/HttpClient;Lorg/ytlegacy/daviskas/core/async/UserAuthorizer;Landroid/content/SharedPreferences;Lorg/ytlegacy/daviskas/core/utils/f;)V

    .line 481
    new-instance v3, Lorg/ytlegacy/daviskas/datalib/v3/ApiRequests;

    const-string v4, "AIzaSyA8eiZmM1FaDVjRy-df2KTyQ_vz_yYM39w"

    sget-object v5, Lorg/ytlegacy/daviskas/app/g;->c:[B

    invoke-direct {v3, v2, v4, v5, v8}, Lorg/ytlegacy/daviskas/datalib/v3/ApiRequests;-><init>(Lorg/ytlegacy/daviskas/datalib/v3/a;Ljava/lang/String;[BLjava/lang/String;)V

    move-object/from16 v0, p0

    iput-object v3, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->r:Lorg/ytlegacy/daviskas/datalib/v3/ApiRequests;

    .line 484
    move-object/from16 v0, p0

    iget-object v2, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->a:Lorg/ytlegacy/daviskas/app/s;

    invoke-virtual {v2}, Lorg/ytlegacy/daviskas/app/s;->P()Z

    move-result v2

    if-eqz v2, :cond_c

    .line 485
    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->U()Lorg/ytlegacy/daviskas/core/async/UserAuthorizer;

    move-result-object v2

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->getPackageName()Ljava/lang/String;

    move-result-object v3

    invoke-static/range {p0 .. p0}, Lorg/ytlegacy/daviskas/core/utils/s;->d(Landroid/content/Context;)Ljava/lang/String;

    move-result-object v4

    move-object/from16 v0, p0

    iget-object v5, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->a:Lorg/ytlegacy/daviskas/app/s;

    invoke-virtual {v5}, Lorg/ytlegacy/daviskas/app/s;->D()Ljava/lang/String;

    move-result-object v5

    invoke-static {v2, v3, v4, v5}, Lorg/ytlegacy/daviskas/core/client/r;->a(Lorg/ytlegacy/daviskas/core/async/UserAuthorizer;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lorg/ytlegacy/daviskas/core/client/r;

    move-result-object v2

    move-object/from16 v0, p0

    iput-object v2, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->s:Lorg/ytlegacy/daviskas/core/client/f;

    .line 495
    :goto_5
    move-object/from16 v0, p0

    iget-object v2, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->s:Lorg/ytlegacy/daviskas/core/client/f;

    new-instance v3, Lorg/ytlegacy/daviskas/app/b/b;

    move-object/from16 v0, p0

    iget-object v4, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->s:Lorg/ytlegacy/daviskas/core/client/f;

    invoke-direct {v3, v4}, Lorg/ytlegacy/daviskas/app/b/b;-><init>(Lorg/ytlegacy/daviskas/core/client/f;)V

    invoke-interface {v2, v3}, Lorg/ytlegacy/daviskas/core/client/f;->a(Lorg/ytlegacy/daviskas/core/client/g;)V

    .line 499
    new-instance v2, Lcom/google/a/a/a;

    const-string v3, "1001680686"

    const-string v4, "8cu4CIrA3gMQrt7R3QM"

    const-string v5, "ConversionPing"

    move-object/from16 v0, p0

    invoke-direct {v2, v0, v3, v4, v5}, Lcom/google/a/a/a;-><init>(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V

    .line 504
    invoke-virtual {v2}, Lcom/google/a/a/a;->a()V

    .line 505
    return-void

    .line 292
    :cond_7
    const/4 v6, 0x0

    goto/16 :goto_0

    .line 327
    :cond_8
    move-object/from16 v0, p0

    iget-object v2, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->f:Lorg/ytlegacy/daviskas/core/client/t;

    invoke-virtual/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->T()Lorg/ytlegacy/daviskas/core/async/a;

    move-result-object v3

    move-object/from16 v0, p0

    iget-object v4, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->a:Lorg/ytlegacy/daviskas/app/s;

    invoke-virtual {v4}, Lorg/ytlegacy/daviskas/app/s;->K()Lorg/ytlegacy/daviskas/core/async/GDataRequest$Version;

    move-result-object v4

    const/4 v5, 0x0

    move-object/from16 v0, v21

    invoke-virtual {v2, v0, v3, v4, v5}, Lorg/ytlegacy/daviskas/core/client/t;->a(Lorg/ytlegacy/daviskas/core/async/DeviceAuthorizer;Lorg/ytlegacy/daviskas/core/async/a;Lorg/ytlegacy/daviskas/core/async/GDataRequest$Version;Lorg/ytlegacy/daviskas/core/async/UserAuthorizer;)V

    goto/16 :goto_1

    .line 350
    :cond_9
    const/4 v2, 0x0

    move-object/from16 v0, p0

    iput-object v2, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->t:Lorg/ytlegacy/daviskas/core/client/bm;

    goto/16 :goto_2

    .line 393
    :cond_a
    sget-object v2, Lorg/ytlegacy/daviskas/core/client/StatParams$Platform;->MOBILE:Lorg/ytlegacy/daviskas/core/client/StatParams$Platform;

    goto/16 :goto_3

    .line 456
    :cond_b
    invoke-static/range {p0 .. p0}, Lorg/ytlegacy/daviskas/app/autosync/a;->b(Landroid/content/Context;)V

    goto/16 :goto_4

    .line 491
    :cond_c
    new-instance v2, Lorg/ytlegacy/daviskas/core/client/az;

    invoke-direct {v2}, Lorg/ytlegacy/daviskas/core/client/az;-><init>()V

    move-object/from16 v0, p0

    iput-object v2, v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->s:Lorg/ytlegacy/daviskas/core/client/f;

    goto :goto_5
.end method

.method public final f()Lorg/ytlegacy/daviskas/core/client/bk;
    .locals 1

    .prologue
    .line 666
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->i:Lorg/ytlegacy/daviskas/core/client/am;

    return-object v0
.end method

.method public final f_()Lorg/ytlegacy/daviskas/core/client/bd;
    .locals 1

    .prologue
    .line 676
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->k:Lorg/ytlegacy/daviskas/core/client/ag;

    return-object v0
.end method

.method protected final j()Ljava/lang/String;
    .locals 3

    .prologue
    .line 226
    invoke-static {p0}, Lorg/ytlegacy/daviskas/core/utils/s;->d(Landroid/content/Context;)Ljava/lang/String;

    move-result-object v0

    iget-object v1, p0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->a:Lorg/ytlegacy/daviskas/app/s;

    invoke-virtual {v1}, Lorg/ytlegacy/daviskas/app/s;->D()Ljava/lang/String;

    move-result-object v1

    iget-object v2, p0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->a:Lorg/ytlegacy/daviskas/app/s;

    invoke-virtual {v2}, Lorg/ytlegacy/daviskas/app/s;->u()Z

    move-result v2

    invoke-static {p0, v0, v1, v2}, Lorg/ytlegacy/daviskas/core/utils/Util;->a(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;Z)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method protected final l()Lorg/ytlegacy/daviskas/core/player/bi;
    .locals 4

    .prologue
    .line 570
    new-instance v0, Lorg/ytlegacy/daviskas/app/prefetch/e;

    invoke-virtual {p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->S()Landroid/content/SharedPreferences;

    move-result-object v1

    invoke-virtual {p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->L()Lorg/ytlegacy/daviskas/core/utils/aj;

    move-result-object v2

    iget-object v3, p0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->a:Lorg/ytlegacy/daviskas/app/s;

    invoke-direct {v0, p0, v1, v2, v3}, Lorg/ytlegacy/daviskas/app/prefetch/e;-><init>(Landroid/content/Context;Landroid/content/SharedPreferences;Lorg/ytlegacy/daviskas/core/utils/f;Lorg/ytlegacy/daviskas/app/s;)V

    iput-object v0, p0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->w:Lorg/ytlegacy/daviskas/app/prefetch/e;

    .line 571
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->w:Lorg/ytlegacy/daviskas/app/prefetch/e;

    invoke-virtual {v0}, Lorg/ytlegacy/daviskas/app/prefetch/e;->b()V

    .line 572
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->w:Lorg/ytlegacy/daviskas/app/prefetch/e;

    invoke-virtual {v0}, Lorg/ytlegacy/daviskas/app/prefetch/e;->d()V

    .line 574
    invoke-super {p0}, Lorg/ytlegacy/daviskas/core/BaseApplication;->l()Lorg/ytlegacy/daviskas/core/player/bi;

    move-result-object v0

    .line 575
    new-instance v1, Lorg/ytlegacy/daviskas/app/prefetch/k;

    iget-object v2, p0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->w:Lorg/ytlegacy/daviskas/app/prefetch/e;

    invoke-direct {v1, v2, v0}, Lorg/ytlegacy/daviskas/app/prefetch/k;-><init>(Lorg/ytlegacy/daviskas/app/prefetch/e;Lorg/ytlegacy/daviskas/core/player/bi;)V

    return-object v1
.end method

.method public final n()Ljava/lang/String;
    .locals 3

    .prologue
    .line 580
    invoke-virtual {p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->getContentResolver()Landroid/content/ContentResolver;

    move-result-object v0

    const-string v1, "youtube_client_id"

    const-string v2, "android-google"

    invoke-static {v0, v1, v2}, Lcom/google/android/gsf/e;->a(Landroid/content/ContentResolver;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public final o()Lorg/ytlegacy/daviskas/app/s;
    .locals 1

    .prologue
    .line 585
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->a:Lorg/ytlegacy/daviskas/app/s;

    return-object v0
.end method

.method public onSharedPreferenceChanged(Landroid/content/SharedPreferences;Ljava/lang/String;)V
    .locals 1

    .prologue
    .line 848
    const-string v0, "prefetch_subscriptions"

    invoke-virtual {v0, p2}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v0

    if-nez v0, :cond_0

    const-string v0, "prefetch_watch_later"

    invoke-virtual {v0, p2}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v0

    if-eqz v0, :cond_1

    .line 850
    :cond_0
    invoke-static {p0}, Lorg/ytlegacy/daviskas/app/prefetch/PrefetchService;->c(Landroid/content/Context;)V

    .line 852
    :cond_1
    return-void
.end method

.method public final p()Ljava/lang/String;
    .locals 1

    .prologue
    .line 590
    const-string v0, "YouTube"

    return-object v0
.end method

.method public final q()V
    .locals 3

    .prologue
    .line 594
    invoke-virtual {p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->S()Landroid/content/SharedPreferences;

    move-result-object v0

    .line 595
    const-string v1, "session_watch_count"

    const/4 v2, 0x0

    invoke-interface {v0, v1, v2}, Landroid/content/SharedPreferences;->getInt(Ljava/lang/String;I)I

    move-result v1

    .line 596
    invoke-static {v0}, Lorg/ytlegacy/daviskas/app/compat/ah;->a(Landroid/content/SharedPreferences;)Lorg/ytlegacy/daviskas/app/compat/ai;

    move-result-object v0

    const-string v2, "session_watch_count"

    add-int/lit8 v1, v1, 0x1

    invoke-virtual {v0, v2, v1}, Lorg/ytlegacy/daviskas/app/compat/ai;->a(Ljava/lang/String;I)Lorg/ytlegacy/daviskas/app/compat/ai;

    move-result-object v0

    invoke-virtual {v0}, Lorg/ytlegacy/daviskas/app/compat/ai;->a()V

    .line 599
    return-void
.end method

.method public final r()Landroid/provider/SearchRecentSuggestions;
    .locals 1

    .prologue
    .line 617
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->b:Landroid/provider/SearchRecentSuggestions;

    return-object v0
.end method

.method public final s()V
    .locals 1

    .prologue
    .line 621
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->c:Lorg/ytlegacy/daviskas/core/suggest/a;

    if-nez v0, :cond_0

    .line 622
    new-instance v0, Lorg/ytlegacy/daviskas/core/suggest/a;

    invoke-direct {v0, p0}, Lorg/ytlegacy/daviskas/core/suggest/a;-><init>(Landroid/content/Context;)V

    iput-object v0, p0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->c:Lorg/ytlegacy/daviskas/core/suggest/a;

    .line 624
    :cond_0
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->c:Lorg/ytlegacy/daviskas/core/suggest/a;

    invoke-virtual {v0}, Lorg/ytlegacy/daviskas/core/suggest/a;->b()V

    .line 625
    return-void
.end method

.method public final t()Lorg/ytlegacy/daviskas/core/async/ck;
    .locals 1

    .prologue
    .line 628
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->d:Lorg/ytlegacy/daviskas/core/async/ck;

    return-object v0
.end method

.method public final u()Lorg/ytlegacy/daviskas/core/async/UserDelegator;
    .locals 1

    .prologue
    .line 632
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->e:Lorg/ytlegacy/daviskas/core/async/UserDelegator;

    return-object v0
.end method

.method public final v()Lorg/ytlegacy/daviskas/datalib/v3/ApiRequests;
    .locals 1

    .prologue
    .line 636
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->r:Lorg/ytlegacy/daviskas/datalib/v3/ApiRequests;

    return-object v0
.end method

.method public final v_()V
    .locals 2

    .prologue
    const/4 v1, 0x0

    .line 793
    invoke-static {}, Lorg/ytlegacy/daviskas/core/L;->b()V

    .line 794
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->u:Lorg/ytlegacy/daviskas/core/client/aw;

    invoke-virtual {v0}, Lorg/ytlegacy/daviskas/core/client/aw;->b()V

    .line 795
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->z:Ljava/util/concurrent/atomic/AtomicReference;

    invoke-virtual {v0, v1}, Ljava/util/concurrent/atomic/AtomicReference;->set(Ljava/lang/Object;)V

    .line 796
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->A:Ljava/util/concurrent/atomic/AtomicReference;

    invoke-virtual {v0, v1}, Ljava/util/concurrent/atomic/AtomicReference;->set(Ljava/lang/Object;)V

    .line 797
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->B:Ljava/util/concurrent/atomic/AtomicReference;

    invoke-virtual {v0, v1}, Ljava/util/concurrent/atomic/AtomicReference;->set(Ljava/lang/Object;)V

    .line 798
    const/4 v0, -0x2

    invoke-virtual {p0, v0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->a(I)V

    .line 799
    invoke-direct {p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->Z()Z

    move-result v0

    if-eqz v0, :cond_0

    .line 800
    invoke-static {p0}, Lorg/ytlegacy/daviskas/app/autosync/a;->b(Landroid/content/Context;)V

    .line 802
    :cond_0
    invoke-virtual {p0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->N()Ljava/util/concurrent/Executor;

    move-result-object v0

    new-instance v1, Lorg/ytlegacy/daviskas/app/r;

    invoke-direct {v1, p0}, Lorg/ytlegacy/daviskas/app/r;-><init>(Lorg/ytlegacy/daviskas/app/YouTubeApplication;)V

    invoke-interface {v0, v1}, Ljava/util/concurrent/Executor;->execute(Ljava/lang/Runnable;)V

    .line 812
    invoke-static {p0}, Lorg/ytlegacy/daviskas/app/prefetch/PrefetchService;->c(Landroid/content/Context;)V

    .line 813
    invoke-static {p0}, Lorg/ytlegacy/daviskas/app/compat/aj;->a(Landroid/content/Context;)V

    .line 814
    return-void
.end method

.method public final w()Lorg/ytlegacy/daviskas/core/client/a;
    .locals 1

    .prologue
    .line 651
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->h:Lorg/ytlegacy/daviskas/core/client/j;

    return-object v0
.end method

.method public final x()Lorg/ytlegacy/daviskas/core/client/f;
    .locals 1

    .prologue
    .line 656
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->s:Lorg/ytlegacy/daviskas/core/client/f;

    return-object v0
.end method

.method public final y()Lorg/ytlegacy/daviskas/core/client/bm;
    .locals 1

    .prologue
    .line 661
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->t:Lorg/ytlegacy/daviskas/core/client/bm;

    return-object v0
.end method

.method public final z()Lorg/ytlegacy/daviskas/core/client/bf;
    .locals 1

    .prologue
    .line 671
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->j:Lorg/ytlegacy/daviskas/core/client/ai;

    return-object v0
.end method
