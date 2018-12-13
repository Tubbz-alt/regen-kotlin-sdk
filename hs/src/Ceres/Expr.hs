module Ceres.Expr where

import XRN.Prelude
import Text.Megaparsec (SourcePos)

data SourceLoc = SourceLoc
  { commentWsStart:: SourcePos
  , start :: SourcePos
  , end :: SourcePos
  } deriving (Eq, Show, Generic)

data Value where
  IntV :: Integer -> Value
  DoubleV :: Double -> Value
  StringV :: Text -> Value
  BoolV :: Bool -> Value
  NilV :: Value
  ObjectV :: HashMap Text Value -> Value
  ListV :: [Value] -> Value
  VectorV :: Vector Value -> Value
  SetV :: HashSet Value -> Value
  deriving (Eq)

data Expr where
  LitE :: Value -> TypeT -> SourceLoc -> Expr
  FunCall :: Expr -> [Expr] -> SourceLoc -> Expr
  FunExpr :: SourceLoc -> Expr
  CaseExpr :: SourceLoc -> Expr
  VarExpr :: Text -> Expr
  PropAccessExpr :: Expr -> Text -> SourceLoc -> Expr
  AndExpr :: Expr -> Expr -> SourceLoc -> Expr
  OrExpr :: Expr -> Expr -> SourceLoc -> Expr
  NotExpr :: Expr -> SourceLoc -> Expr

data TypeT where
  NilT :: TypeT
  StringT :: TypeT
  BoolT :: TypeT
  IntegerT :: TypeT
  DoubleT :: TypeT
  ObjT :: HashMap Text Type -> Expr -> TypeT
  SetT :: TypeT -> TypeT
  List :: TypeT -> TypeT
  VectorT :: TypeT -> TypeT
  StructT :: Text -> HashMap Text Type -> Expr -> TypeT
  FunT :: FunType -> TypeT
  RefinementT :: TypeT -> Expr -> Text -> TypeT
  UnionT :: [TypeT] -> TypeT
  TypeT :: TypeT

data FunType = FunType
  { params :: [(Text, Expr)]
  , ret :: Expr
  }

data SMTExpr =
  SMTSym Text |
  SMTList [SMTExpr]

data Env = Env
  { bindings :: HashMap Text Checked
  -- , smtEngine
  , smtAssertions :: [SMTExpr]
  }

data Checked = Checked
  { typ :: TypeT
  , value :: Maybe ()
  -- , cost
  , smtEncoding :: Maybe SMTExpr
  }

type TypeErrors = [(Text, Maybe Expr)]

type CheckResult = Either TypeErrors Checked

data EvalLevel = None | Eval | SMTEncode

class Monad m => MonadMeter m where
  consumeGas :: Word32 -> m ()

class Monad m => MonadTrackUsage m where
  trackUsage :: Text -> Expr -> m ()

typeCheck :: (MonadReader Env m, MonadMeter m, MonadTrackUsage m) => Expr -> EvalLevel -> m CheckResult
typeCheck = error "TODO"
